import React, { useState } from 'react';

const categories = [
  { label: 'All Categories', value: 'all' },
  { label: 'Textbooks', value: 'textbooks' },
  { label: 'Electronics', value: 'electronics' },
  { label: 'Furniture', value: 'furniture' },
  { label: 'Other', value: 'other' },
];

const priceRanges = [
  { label: 'All Prices', value: 'all' },
  { label: 'Under $50', value: 'under50' },
  { label: '$50-$200', value: '50to200' },
  { label: 'Over $200', value: 'over200' },
];

const listings = [
  {
    id: 1,
    category: 'Electronics',
    title: 'MacBook Pro 14\" M2 - Excellent Condition',
    condition: 'Like New',
    date: '2025-10-30',
    description: 'Used for 6 months, comes with original box and charger. Perfect for CS students!',
    image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=400&q=80',
    favorite: false,
  },
  {
    id: 2,
    category: 'Textbooks',
    title: 'Calculus Early Transcendentals 8th Ed',
    condition: 'Good',
    date: '2025-10-29',
    description: 'Math textbook for MATH 30/31. Minimal highlighting, all pages intact.',
    image: 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=400&q=80',
    favorite: true,
  },
  {
    id: 3,
    category: 'Electronics',
    title: 'Gaming Headset',
    condition: 'Like New',
    date: '2025-10-28',
    description: 'Barely used, great sound quality. Includes mic.',
    image: 'https://images.unsplash.com/photo-1519125323398-675f0ddb6308?auto=format&fit=crop&w=400&q=80',
    favorite: false,
  },
  // Add more mock listings as needed
];

export function MarketplacePage() {
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [selectedPrice, setSelectedPrice] = useState('all');
  const [search, setSearch] = useState('');

  const filteredListings = listings.filter(listing => {
    const matchesCategory = selectedCategory === 'all' || listing.category.toLowerCase() === selectedCategory;
    const matchesSearch = listing.title.toLowerCase().includes(search.toLowerCase()) || listing.description.toLowerCase().includes(search.toLowerCase());
    // Price filtering is mock only
    return matchesCategory && matchesSearch;
  });

  return (
    <div className="min-h-screen bg-white">
      <header className="border-b px-8 py-4 flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">CampusConnect</h1>
          <p className="text-sm text-gray-500">Campus Marketplace</p>
        </div>
        <button className="bg-blue-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-blue-700">+ Create Listing</button>
      </header>
      <main className="px-8 py-6">
        <h2 className="text-2xl font-bold mb-2">Campus Marketplace</h2>
        <p className="text-gray-600 mb-6">Browse 8 listings from your fellow Spartans</p>
        <div className="flex gap-2 mb-4">
          {categories.map(cat => (
            <button
              key={cat.value}
              className={`px-4 py-2 rounded-full border flex items-center gap-2 font-medium ${selectedCategory === cat.value ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-700'}`}
              onClick={() => setSelectedCategory(cat.value)}
            >
              {cat.label}
            </button>
          ))}
        </div>
        <div className="flex gap-2 mb-6">
          <input
            type="text"
            placeholder="Search for textbooks, electronics, furniture..."
            className="flex-1 px-4 py-2 rounded-lg border bg-gray-50"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
          <select
            className="px-4 py-2 rounded-lg border bg-gray-50"
            value={selectedPrice}
            onChange={e => setSelectedPrice(e.target.value)}
          >
            {priceRanges.map(pr => (
              <option key={pr.value} value={pr.value}>{pr.label}</option>
            ))}
          </select>
        </div>
        <p className="text-gray-500 mb-2">Showing {filteredListings.length} listings</p>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {filteredListings.map(listing => (
            <div key={listing.id} className="bg-white rounded-xl shadow border p-4">
              <div className="relative">
                <img src={listing.image} alt={listing.title} className="rounded-lg w-full h-40 object-cover mb-2" />
                <span className="absolute top-2 left-2 bg-blue-600 text-white text-xs px-2 py-1 rounded">{listing.category}</span>
                <button className="absolute top-2 right-2 bg-white rounded-full p-1 shadow">
                  {listing.favorite ? (
                    <span role="img" aria-label="favorite">❤️</span>
                  ) : (
                    <span role="img" aria-label="not favorite">🤍</span>
                  )}
                </button>
              </div>
              <h3 className="font-semibold text-lg mb-1">{listing.title}</h3>
              <div className="flex items-center gap-2 mb-1">
                <span className="bg-gray-200 text-xs px-2 py-1 rounded">{listing.condition}</span>
                <span className="text-xs text-gray-400">{listing.date}</span>
              </div>
              <p className="text-gray-600 text-sm mb-2">{listing.description}</p>
              <div className="flex gap-2">
                <button className="bg-gray-100 px-3 py-1 rounded text-sm">Contact Seller</button>
                <button className="bg-gray-100 px-3 py-1 rounded text-sm">Report</button>
              </div>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
}
