'use client'

import React, { useState } from 'react'
import { useRouter } from 'next/navigation'

export default function LoginForm({ onLogin }) {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const router = useRouter()

    function handleSubmit(e) {
        e.preventDefault()
        setError('')

        // Simple client-side validation
        if (!email || !password) {
            setError('Please provide both email and password.')
            return
        }

        // In a real app you would call an API here.
        // We'll simulate success and call onLogin callback.
        const fakeUser = { name: 'Puneeth Kumar', email }
        onLogin(fakeUser)
        router.push('/dashboard')
    }

    return (
        <div className="max-w-md mx-auto bg-white rounded-2xl shadow p-6">
            <h2 className="text-2xl font-semibold mb-2">Sign in</h2>
            <p className="text-sm text-gray-500 mb-4">Welcome back — login to manage your skills.</p>

            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium mb-1">Email</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="w-full border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-300"
                        required
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium mb-1">Password</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="w-full border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-300"
                        required
                    />
                </div>

                {error && <p className="text-sm text-red-500">{error}</p>}

                <button className="w-full py-2 rounded-md bg-indigo-600 text-white font-medium">Sign In</button>
            </form>
        </div>
    )
}