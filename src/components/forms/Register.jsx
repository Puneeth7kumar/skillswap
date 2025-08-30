'use client'

import React, { useState } from 'react'
import { useRouter } from 'next/navigation'

export default function RegisterForm({ onRegister }) {
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const router = useRouter()

    function handleSubmit(e) {
        e.preventDefault()
        setError('')

        if (!name || !email || !password) {
            setError('Please complete all fields.')
            return
        }

        // Simulate registration
        const createdUser = { name, email }
        onRegister(createdUser)
        router.push('/dashboard')
    }

    return (
        <div className="max-w-md mx-auto bg-white rounded-2xl shadow p-6">
            <h2 className="text-2xl font-semibold mb-2">Create account</h2>
            <p className="text-sm text-gray-500 mb-4">Start sharing your skills with the community.</p>

            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium mb-1">Full name</label>
                    <input
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className="w-full border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-300"
                        required
                    />
                </div>

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

                <button className="w-full py-2 rounded-md bg-indigo-600 text-white font-medium">Create account</button>
            </form>
        </div>
    )
}