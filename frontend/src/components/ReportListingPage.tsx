import React, { useState } from 'react';

const stats = [
  { label: 'Pending Reports', value: 12, color: 'bg-orange-100', icon: '⏰' },
  { label: 'High Priority', value: 3, color: 'bg-red-100', icon: '❗' },
  { label: 'Resolved Today', value: 8, color: 'bg-green-100', icon: '✅' },
  { label: 'Dismissed', value: 5, color: 'bg-gray-100', icon: '✖️' },
];

const reports = [
  {
    id: 'RPT-001',
    title: 'iPhone 13 Pro - Barely Used',
    severity: 'medium',
    status: 'pending',
    description: 'Incomplete listing - No price information',
    reporter: 'john.doe@sjsu.edu',
    seller: 'seller123@sjsu.edu',
    date: '2025-10-30',
    image: 'https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=400&q=80',
  },
  // Add more mock reports as needed
];

export function ReportListingPage() {
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('all');
  const [severity, setSeverity] = useState('all');

  const filteredReports = reports.filter(report => {
    const matchesSearch = report.title.toLowerCase().includes(search.toLowerCase()) || report.description.toLowerCase().includes(search.toLowerCase());
    // Status/severity filtering is mock only
    return matchesSearch;
  });

  return (
    <div className="min-h-screen bg-white">
      <header className="border-b px-8 py-4 flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">CampusConnect</h1>
          <p className="text-sm text-gray-500">Campus Marketplace</p>
        </div>
      </header>
      <main className="px-8 py-6">
        <h2 className="text-2xl font-bold mb-2 flex items-center gap-2">
          <span className="text-red-500">⚠️</span> Report Management
        </h2>
        <p className="text-gray-600 mb-6">Review and moderate reported listings from the campus community</p>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
          {stats.map(stat => (
            <div key={stat.label} className={`rounded-lg p-4 flex flex-col items-center ${stat.color}`}>
              <span className="text-2xl mb-2">{stat.icon}</span>
              <span className="text-2xl font-bold">{stat.value}</span>
              <span className="text-sm text-gray-700">{stat.label}</span>
            </div>
          ))}
        </div>
        <div className="flex gap-2 mb-6">
          <input
            type="text"
            placeholder="Search by report ID, listing title, or reporter..."
            className="flex-1 px-4 py-2 rounded-lg border bg-gray-50"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
          <select
            className="px-4 py-2 rounded-lg border bg-gray-50"
            value={status}
            onChange={e => setStatus(e.target.value)}
          >
            <option value="all">All Status</option>
            <option value="pending">Pending</option>
            <option value="resolved">Resolved</option>
            <option value="dismissed">Dismissed</option>
          </select>
          <select
            className="px-4 py-2 rounded-lg border bg-gray-50"
            value={severity}
            onChange={e => setSeverity(e.target.value)}
          >
            <option value="all">All Severity</option>
            <option value="low">Low</option>
            <option value="medium">Medium</option>
            <option value="high">High</option>
          </select>
        </div>
        <h3 className="text-lg font-semibold mb-2">Reported Listings</h3>
        <div className="space-y-4">
          {filteredReports.map(report => (
            <div key={report.id} className="bg-white rounded-xl shadow border p-4 flex gap-4 items-center">
              <img src={report.image} alt={report.title} className="rounded-lg w-32 h-24 object-cover" />
              <div className="flex-1">
                <h4 className="font-semibold text-md mb-1">{report.title} <span className="bg-orange-100 text-orange-800 text-xs px-2 py-1 rounded">{report.severity}</span></h4>
                <div className="text-xs text-gray-500 mb-1">Report ID: {report.id}</div>
                <div className="flex items-center gap-2 mb-1">
                  <span className="text-gray-600 text-sm">⚠️ {report.description}</span>
                  <span className="bg-yellow-100 text-yellow-800 text-xs px-2 py-1 rounded">{report.status}</span>
                </div>
                <div className="text-xs text-gray-500 mb-1">Reported by: {report.reporter}</div>
                <div className="text-xs text-gray-500 mb-1">Seller: {report.seller}</div>
                <div className="text-xs text-gray-400 mb-2">Date: {report.date}</div>
                <div className="flex gap-2">
                  <button className="bg-blue-100 text-blue-800 px-3 py-1 rounded text-sm">View Details</button>
                  <button className="bg-green-100 text-green-800 px-3 py-1 rounded text-sm">Resolve</button>
                  <button className="bg-red-100 text-red-800 px-3 py-1 rounded text-sm">Remove Listing</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
}
