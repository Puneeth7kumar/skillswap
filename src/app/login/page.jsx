'use client'

import React, { useState } from 'react'
import { useRouter } from 'next/navigation'

export default function LoginForm({ onLogin }) {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)
    const router = useRouter()

    async function handleSubmit(e) {
        e.preventDefault()
        setError('')
        setLoading(true)

        if (!email || !password) {
            setError('Please provide both email and password.')
            setLoading(false)
            return
        }

        try {
            const res = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
            })

            let data
            try {
                data = await res.json()
            } catch {
                throw new Error('Server did not return valid JSON')
            }

            if (!res.ok) throw new Error(data.message || 'Login failed')

            // Save JWT token and user info
            localStorage.setItem('token', data.token)
            const userData = { id: data.id, name: data.name, email: data.email }
            localStorage.setItem('user', JSON.stringify(userData))

            // Update parent state
            onLogin && onLogin(userData)

            router.push('/') // redirect
        } catch (err) {
            setError(err.message)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-b from-indigo-300 to-white px-4">
            <div className="max-w-md w-full bg-white rounded-3xl shadow-lg p-8">
                <h1 className="text-3xl font-bold text-gray-800 mb-2">Sign In</h1>
                <p className="text-gray-500 mb-6">Welcome back — login to manage your skills.</p>

                <form onSubmit={handleSubmit} className="space-y-5">
                    <div>
                        <label className="block text-gray-700 text-sm font-medium mb-1">Email</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="Enter Email"
                            className="w-full border text-gray-950 border-gray-500 rounded-xl px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-indigo-400"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-gray-700 text-sm font-medium mb-1">Password</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="********"
                            className="w-full border text-gray-950 border-gray-500 rounded-xl px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-indigo-400"
                            required
                        />
                    </div>

                    {error && <p className="text-sm text-red-500">{error}</p>}

                    <button
                        type="submit"
                        disabled={loading}
                        className={`w-full py-3 rounded-xl text-white font-semibold transition ${loading ? 'bg-indigo-300 cursor-not-allowed' : 'bg-indigo-600 hover:bg-indigo-700'
                            }`}
                    >
                        {loading ? 'Signing In...' : 'Sign In'}
                    </button>
                </form>

                <p className="text-sm text-gray-500 mt-6 text-center">
                    Don't have an account?{' '}
                    <a href="/register" className="text-indigo-600 font-medium hover:underline">
                        Create one
                    </a>
                </p>
            </div>
        </div>
    )
}
