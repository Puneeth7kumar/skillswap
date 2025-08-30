'use client'

import React, { useState, useEffect } from 'react'
import { motion } from 'framer-motion'
import Navbar from '@/components/Navbar'
import SkillCard from '@/components/SkillCard'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import toast, { Toaster } from 'react-hot-toast'

export default function Page() {
  const [user, setUser] = useState(null)
  const [skills, setSkills] = useState([])
  const [requests, setRequest] = useState([])
  const [search, setSearch] = useState('')
  const [newSkill, setNewSkill] = useState({ title: '', description: '', level: '' })
  const [loading, setLoading] = useState(false)
  const [darkMode, setDarkMode] = useState(false)

  // Load dark mode preference from localStorage
  useEffect(() => {
    const savedMode = localStorage.getItem('darkMode')
    setDarkMode(savedMode === 'true')

    const savedUser = localStorage.getItem('user')
    if (savedUser) setUser(JSON.parse(savedUser))
  }, [])

  // Persist dark mode to localStorage whenever it changes
  const toggleDarkMode = () => {
    setDarkMode(prev => {
      localStorage.setItem('darkMode', !prev)
      return !prev
    })
  }

  // Fetch skills and requests on mount
  useEffect(() => {
    fetchSkills()
    fetchRequest()
  }, [])

  async function fetchSkills() {
    try {
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/skills`)
      const data = await res.json()
      setSkills(data.content || data)
    } catch (err) {
      console.error('Error fetching skills:', err)
    }
  }

  async function fetchRequest() {
    try {
      const token = localStorage.getItem('token')
      if (!token) return

      const resMade = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/requests/made`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      const madeRequests = await resMade.json()

      const resReceived = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/requests/received`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      const receivedRequests = await resReceived.json()

      setRequest([...madeRequests, ...receivedRequests])
    } catch (err) {
      console.error('Error fetching request:', err)
    }
  }

  function handleLogout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }

  async function handleRequest(skill) {
    const token = localStorage.getItem('token')
    if (!token) {
      alert('Please log in to send a request')
      return
    }

    try {
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/requests`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          skillId: skill.id,
          message: `Hi, I’d like to learn ${skill.title}!`,
        }),
      })

      const data = await res.json()
      if (!res.ok) throw new Error(data.message || 'Failed to send request')

      toast.success(`Request sent successfully for "${skill.title}" ✅`)
    } catch (err) {
      toast.error(err.message)
    }
  }

  async function handleAddSkill() {
    if (!newSkill.title || !newSkill.description || !newSkill.level) return

    setLoading(true)
    try {
      const token = localStorage.getItem('token')
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/skills`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(newSkill),
      })
      const data = await res.json()
      if (!res.ok) throw new Error(data.message || 'Error adding skill')

      setSkills([data, ...skills])
      setNewSkill({ title: '', description: '', level: '' })
      toast.success(`Skill "${data.title}" added successfully! 🎉`)
    } catch (err) {
      toast.error(err.message)
    } finally {
      setLoading(false)
    }
  }

  const filteredSkills = skills.filter((s) =>
    s.title.toLowerCase().includes(search.toLowerCase())
  )

  async function handleSearch() {
    if (!search.trim()) {
      fetchSkills()
      return
    }

    try {
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/api/skills/search?keyword=${encodeURIComponent(search)}`
      )
      const data = await res.json()
      setSkills(data)
    } catch (err) {
      console.error('Error searching skills:', err)
      toast.error('Failed to search skills')
    }
  }

  return (
    <div className={`${darkMode ? 'dark' : ''}`}>
      <div className="min-h-screen bg-gradient-to-b from-gray-200 to-indigo-200 dark:from-gray-900 dark:to-gray-950 text-gray-900 dark:text-white transition-colors duration-500">
        <Toaster position="top-right" reverseOrder={false} />
        <Navbar
          user={user}
          onLogout={handleLogout}
          darkMode={darkMode}
          toggleDarkMode={toggleDarkMode}
        />

        <main className="max-w-7xl mx-auto px-6 py-12">
          {/* Hero Section */}
          <motion.section
            className="grid md:grid-cols-2 gap-12 items-center mb-16"
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
          >
            <div>
              <h1 className="text-5xl md:text-6xl font-extrabold mb-4 bg-clip-text text-transparent bg-gradient-to-r from-purple-400 via-pink-400 to-red-400">
                Share Skills, Learn Together
              </h1>
              <p className="text-gray-700 dark:text-gray-300 mb-6 text-lg">
                Connect with peers to exchange knowledge and grow your skillset. Share what you know, learn what you need.
              </p>
              <div className="flex gap-3">
                <Input
                  type="text"
                  placeholder="Search skills..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                  className="w-full border-gray-300 dark:bg-gray-800 dark:text-white dark:border-gray-600"
                  onKeyDown={(e) => {
                    if (e.key === 'Enter') handleSearch()
                  }}
                />
                <Button onClick={handleSearch} className="bg-purple-500 hover:bg-purple-600">
                  Search
                </Button>
              </div>
            </div>
            <motion.div
              className="hidden md:block bg-white dark:bg-gray-800 p-6 rounded-3xl shadow-2xl"
              initial={{ x: 50, opacity: 0 }}
              animate={{ x: 0, opacity: 1 }}
              transition={{ duration: 0.6 }}
            >
              <h4 className="font-semibold text-lg mb-4">Quick Stats</h4>
              <div className="grid grid-cols-3 gap-4 text-center">
                <div>
                  <div className="text-3xl font-bold text-purple-500">{skills.length}</div>
                  <div className="text-gray-500 dark:text-gray-400 text-sm">Skills</div>
                </div>
                <div>
                  <div className="text-3xl font-bold text-purple-500">{requests.length}</div>
                  <div className="text-gray-500 dark:text-gray-400 text-sm">Requests</div>
                </div>
                <div>
                  <div className="text-3xl font-bold text-purple-500">0</div>
                  <div className="text-gray-500 dark:text-gray-400 text-sm">Messages</div>
                </div>
              </div>
            </motion.div>
          </motion.section>

          {/* Add Skill Section */}
          {user && (
            <motion.section
              className="mb-16 p-8 rounded-3xl bg-gradient-to-r from-purple-400 via-pink-400 to-red-400 shadow-2xl text-white"
              initial={{ opacity: 0, y: 30 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6 }}
            >
              <h2 className="text-3xl font-bold mb-6 text-center">Add a New Skill</h2>
              <div className="grid md:grid-cols-3 gap-4">
                <Input
                  placeholder="Skill Title"
                  value={newSkill.title}
                  onChange={(e) => setNewSkill({ ...newSkill, title: e.target.value })}
                  className="bg-white text-black dark:bg-gray-200 dark:text-black dark:border-gray-600"
                />
                <Input
                  placeholder="Skill Level"
                  value={newSkill.level}
                  onChange={(e) => setNewSkill({ ...newSkill, level: e.target.value })}
                  className="bg-white text-black dark:bg-gray-200 dark:text-black dark:border-gray-600"
                />
                <Textarea
                  placeholder="Description"
                  value={newSkill.description}
                  onChange={(e) => setNewSkill({ ...newSkill, description: e.target.value })}
                  className="bg-white text-black dark:bg-gray-200 dark:text-black dark:border-gray-600"
                />
              </div>
              <Button
                className="mt-6 w-full md:w-auto bg-white text-purple-500 font-bold hover:bg-gray-100 dark:hover:bg-gray-700"
                onClick={handleAddSkill}
                disabled={loading}
              >
                {loading ? 'Adding...' : 'Add Skill'}
              </Button>
            </motion.section>
          )}

          {/* Skill Cards */}
          <motion.section
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.6 }}
          >
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-semibold text-purple-500">Available Skills</h2>
              <p className="text-gray-500 dark:text-gray-400 text-sm">
                Browse skills offered by our community
              </p>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {filteredSkills.map((skill) => (
                <motion.div
                  key={skill.id}
                  whileHover={{ scale: 1.05 }}
                  transition={{ type: 'spring', stiffness: 300 }}
                >
                  <SkillCard skill={skill} onRequest={handleRequest} darkMode={darkMode} />
                </motion.div>
              ))}
            </div>
          </motion.section>
        </main>

        <footer className="text-center py-6 text-gray-500 dark:text-gray-400 border-t border-gray-300 dark:border-gray-700">
          © {new Date().getFullYear()} SkillSwap Hub — Built with ❤️
        </footer>
      </div>
    </div>
  )
}
