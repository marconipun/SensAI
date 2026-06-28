import React, { useState, useEffect } from 'react';
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell
} from 'recharts';

const Dashboard = () => {
  const [dailyData, setDailyData] = useState(null);
  const [weeklyData, setWeeklyData] = useState([]);
  const [gaps, setGaps] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch data from backend
  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        // Get token from localStorage (set by Member 5's auth)
        const token = localStorage.getItem('token') || '';
        const headers = { Authorization: `Bearer ${token}` };

        const [dailyRes, weeklyRes, gapsRes] = await Promise.all([
          fetch('/api/nutrition/daily', { headers }),
          fetch('/api/nutrition/weekly', { headers }),
          fetch('/api/nutrition/gaps', { headers })
        ]);

        if (!dailyRes.ok || !weeklyRes.ok || !gapsRes.ok) {
          throw new Error('Failed to fetch nutrition data');
        }

        const daily = await dailyRes.json();
        const weekly = await weeklyRes.json();
        const gaps = await gapsRes.json();

        setDailyData(daily);
        setWeeklyData(weekly);
        setGaps(gaps);
      } catch (err) {
        console.error('Dashboard fetch error:', err);
        setError('Unable to load nutrition data. Please log a meal first.');
        // Optionally, you can set fallback mock data here for demo purposes.
        // But we'll leave it empty to show the "no data" state.
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleLogFromPlan = async () => {
    try {
      const token = localStorage.getItem('token') || '';
      const res = await fetch('/api/nutrition/log-from-plan', {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` }
      });
      if (res.ok) {
        // Refresh data after logging
        window.location.reload();
      } else {
        alert('Could not log from plan. Make sure you have an active meal plan.');
      }
    } catch (err) {
      alert('Error logging from plan. Please try again.');
    }
  };

  // Show loading state
  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen bg-gradient-to-br from-indigo-50 to-purple-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-4 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600 font-medium">Loading your nutrition passport...</p>
        </div>
      </div>
    );
  }

  // Show error state if fetch failed and no data
  if (error && !dailyData) {
    return (
      <div className="flex items-center justify-center h-screen bg-gradient-to-br from-indigo-50 to-purple-50 p-6">
        <div className="bg-white/70 backdrop-blur-xl rounded-3xl p-8 max-w-md text-center shadow-xl border border-white/20">
          <h2 className="text-2xl font-bold text-gray-700">No Data Yet</h2>
          <p className="text-gray-500 mt-2">{error}</p>
          <p className="text-sm text-gray-400 mt-4">Start by logging a meal or generating a meal plan.</p>
          <button
            onClick={handleLogFromPlan}
            className="mt-6 bg-indigo-600 text-white px-6 py-2 rounded-xl hover:bg-indigo-700 transition"
          >
            Log from Plan
          </button>
        </div>
      </div>
    );
  }

  // If we have data (or partial data), render the full dashboard.
  // Use default values if some fields are missing.
  const daily = dailyData || { calories: 0, calorieTarget: 2000, proteinG: 0, carbsG: 0, fatG: 0, completionPercentage: 0 };
  const weekly = weeklyData.length ? weeklyData : [];
  const gapsList = gaps.length ? gaps : [];

  // Macro data for waterfall bars
  const macroData = [
    { name: 'Protein', value: daily.proteinG || 0, target: daily.proteinTarget || 150, color: '#4F46E5' },
    { name: 'Carbs', value: daily.carbsG || 0, target: daily.carbsTarget || 250, color: '#10B981' },
    { name: 'Fat', value: daily.fatG || 0, target: daily.fatTarget || 65, color: '#F59E0B' }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-50 via-purple-50 to-pink-50 p-6 relative">
      <div className="max-w-7xl mx-auto relative z-10">
        {/* Header */}
        <h1 className="text-4xl font-bold text-gray-800 mb-8 tracking-tight">
          Your <span className="text-transparent bg-clip-text bg-gradient-to-r from-indigo-600 to-purple-600">Nutrition Passport</span>
        </h1>

        {/* Liquid Ring – Glass Card */}
        <div className="bg-white/70 backdrop-blur-xl rounded-3xl p-8 mb-8 shadow-xl border border-white/20 transition-all duration-300 hover:shadow-2xl">
          <div className="flex flex-col md:flex-row items-center justify-between gap-6">
            <div>
              <h2 className="text-2xl font-semibold text-gray-700">Today's Nutrition Score</h2>
              <div className="text-6xl font-bold text-indigo-600 mt-2">
                {daily.completionPercentage || 0}%
              </div>
              <div className="text-sm text-gray-500 mt-1">
                {daily.calories || 0} of {daily.calorieTarget || 0} calories
              </div>
            </div>
            <div className="relative w-40 h-40">
              <svg className="w-40 h-40 transform -rotate-90">
                <defs>
                  <linearGradient id="ringGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" stopColor="#4F46E5" />
                    <stop offset="100%" stopColor="#7C3AED" />
                  </linearGradient>
                </defs>
                <circle cx="80" cy="80" r="68" stroke="#E5E7EB" strokeWidth="12" fill="none" />
                <circle
                  cx="80" cy="80" r="68"
                  stroke="url(#ringGradient)"
                  strokeWidth="12"
                  fill="none"
                  strokeDasharray={`${((daily.completionPercentage || 0) / 100) * 2 * Math.PI * 68} ${2 * Math.PI * 68}`}
                  strokeLinecap="round"
                  className="transition-all duration-1000 ease-out"
                />
              </svg>
              <div className="absolute inset-0 flex items-center justify-center flex-col">
                <span className="text-sm font-medium text-gray-500">Score</span>
                <span className="text-2xl font-bold text-gray-700">{daily.completionPercentage || 0}%</span>
              </div>
            </div>
          </div>
        </div>

        {/* Log Button – Glass */}
        <button
          onClick={handleLogFromPlan}
          className="w-full bg-gradient-to-r from-indigo-600 to-purple-600 text-white py-4 rounded-2xl mb-8 shadow-lg hover:shadow-xl transition-all duration-300 font-medium text-lg backdrop-blur-sm hover:scale-[1.02] active:scale-[0.98]"
        >
          ⚡ Log Today's Meals from Your Plan
        </button>

        {/* Macro Waterfall + Weekly Chart – Glass Cards */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          {/* Waterfall Macro Bars */}
          <div className="bg-white/70 backdrop-blur-xl rounded-3xl p-6 shadow-xl border border-white/20">
            <h3 className="text-xl font-semibold text-gray-700 mb-6">Macro Waterfall</h3>
            <div className="space-y-5">
              {macroData.map((macro) => {
                const percent = Math.min((macro.value / macro.target) * 100, 100);
                return (
                  <div key={macro.name} className="space-y-1">
                    <div className="flex justify-between text-sm">
                      <span className="font-medium text-gray-600">{macro.name}</span>
                      <span className="text-gray-500">{macro.value}g / {macro.target}g</span>
                    </div>
                    <div className="relative h-6 bg-gray-200/50 rounded-full overflow-hidden backdrop-blur-sm">
                      <div
                        className="h-full rounded-full transition-all duration-1000 ease-out"
                        style={{
                          width: `${percent}%`,
                          background: `linear-gradient(90deg, ${macro.color}dd, ${macro.color})`,
                          boxShadow: `0 0 20px ${macro.color}44`
                        }}
                      />
                      <div className="absolute inset-0 flex items-center justify-end pr-2 text-xs font-bold text-white drop-shadow-md">
                        {Math.round(percent)}%
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Weekly Chart */}
          <div className="bg-white/70 backdrop-blur-xl rounded-3xl p-6 shadow-xl border border-white/20">
            <h3 className="text-xl font-semibold text-gray-700 mb-4">Weekly Calorie Flow</h3>
            {weekly.length > 0 ? (
              <div className="h-56">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={weekly}>
                    <XAxis dataKey="date" tickFormatter={date => new Date(date).getDate()} tick={{ fill: '#6B7280' }} />
                    <YAxis tick={{ fill: '#6B7280' }} />
                    <Tooltip contentStyle={{ background: 'rgba(255,255,255,0.8)', backdropFilter: 'blur(8px)', borderRadius: '12px', border: 'none' }} />
                    <Bar dataKey="calories" fill="url(#barGradient)" radius={[8, 8, 0, 0]}>
                      <defs>
                        <linearGradient id="barGradient" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%" stopColor="#4F46E5" />
                          <stop offset="100%" stopColor="#818CF8" />
                        </linearGradient>
                      </defs>
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              </div>
            ) : (
              <div className="h-56 flex items-center justify-center text-gray-400">
                No weekly data yet
              </div>
            )}
          </div>
        </div>

        {/* Magic Gaps Cards */}
        {gapsList.length > 0 && (
          <div className="bg-white/70 backdrop-blur-xl rounded-3xl p-6 shadow-xl border border-white/20 mb-8">
            <h3 className="text-xl font-semibold text-gray-700 mb-4">✨ Nutrition Opportunities</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {gapsList.map((gap, idx) => (
                <div key={idx} className="bg-amber-50/80 backdrop-blur-sm border border-amber-200/50 rounded-2xl p-5 shadow-sm">
                  <div className="flex justify-between items-center">
                    <span className="font-medium text-gray-700">{gap.nutrientName}</span>
                    <span className="text-sm font-semibold text-amber-600">{gap.percentAchieved || 0}%</span>
                  </div>
                  <div className="w-full bg-amber-200/50 rounded-full h-2.5 mt-2 overflow-hidden">
                    <div
                      className="bg-gradient-to-r from-amber-400 to-amber-500 rounded-full h-2.5 transition-all duration-1000"
                      style={{ width: `${gap.percentAchieved || 0}%` }}
                    />
                  </div>
                  <p className="text-sm text-amber-700 mt-3">{gap.suggestion || 'Add more nutrients'}</p>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Health Pulse Wave – animated SVG at bottom */}
        <div className="relative w-full h-16 overflow-hidden mt-8">
          <div className="absolute inset-0 flex items-end">
            <svg className="w-full h-12" viewBox="0 0 1200 120" preserveAspectRatio="none">
              <defs>
                <linearGradient id="waveGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                  <stop offset="0%" stopColor="#4F46E5" stopOpacity="0.3" />
                  <stop offset="100%" stopColor="#4F46E5" stopOpacity="0.05" />
                </linearGradient>
              </defs>
              <path
                d="M0,60 C300,20 600,100 900,60 C1050,40 1150,60 1200,40 L1200,120 L0,120 Z"
                fill="url(#waveGradient)"
                className="animate-wave"
              />
              <path
                d="M0,80 C400,40 800,120 1200,80 L1200,120 L0,120 Z"
                fill="url(#waveGradient)"
                opacity="0.5"
                className="animate-wave-slow"
              />
            </svg>
          </div>
          <style jsx>{`
            @keyframes wave {
              0% { transform: translateX(0); }
              100% { transform: translateX(-100px); }
            }
            @keyframes wave-slow {
              0% { transform: translateX(0); }
              100% { transform: translateX(50px); }
            }
            .animate-wave {
              animation: wave 4s ease-in-out infinite alternate;
            }
            .animate-wave-slow {
              animation: wave-slow 6s ease-in-out infinite alternate;
            }
          `}</style>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;