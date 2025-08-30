import React, { useState, useEffect, useRef } from 'react'
import Link from 'next/link'
import { Button } from '@/components/ui/button'
import { HiMenu, HiX } from 'react-icons/hi'
import { FaBell } from 'react-icons/fa'

export default function Navbar({ user, onLogout, darkMode, toggleDarkMode }) {
    const [mobileOpen, setMobileOpen] = useState(false)
    const [mounted, setMounted] = useState(false)
    const [notifications, setNotifications] = useState([])
    const [showNotifications, setShowNotifications] = useState(false)
    const avatarLetter = user?.name?.[0].toUpperCase() || ''

    const notificationRef = useRef(null) // Ref for notifications dropdown

    useEffect(() => {
        if (!user) return
        const token = localStorage.getItem('token')
        fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/notifications/notifications`, {
            headers: { Authorization: `Bearer ${token}` },
        })
            .then(res => res.json())
            .then(data => setNotifications(data))
            .catch(err => console.error(err))
    }, [user])
    const clearAll = () => {
        const token = localStorage.getItem('token');
        fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/notifications/clear`, {
            method: 'DELETE',
            headers: { Authorization: `Bearer ${token}` },
        }).then(() => setNotifications([]))
            .catch(err => console.error(err));
    };
    // Click outside to close dropdown
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (notificationRef.current && !notificationRef.current.contains(event.target)) {
                setShowNotifications(false)
            }
        }
        document.addEventListener('mousedown', handleClickOutside)
        return () => {
            document.removeEventListener('mousedown', handleClickOutside)
        }
    }, [])

    useEffect(() => {
        setMounted(true)
    }, [])

    if (!mounted) return null

    return (
        <header className={`sticky top-0 z-50 transition-colors duration-500 ${darkMode ? 'bg-gray-800 text-white' : 'bg-gradient-to-r from-purple-400 via-pink-400 to-red-400 text-white'} shadow-md`}>
            <div className="max-w-7xl mx-auto px-6 lg:px-8">
                <div className="flex justify-between items-center h-16">
                    <Link href="/" className="text-2xl md:text-3xl font-extrabold tracking-tight hover:text-yellow-300 transition">
                        SkillSwap Hub
                    </Link>

                    <nav className="hidden md:flex items-center gap-4">
                        <Link href="/" className="px-3 py-2 rounded-md hover:bg-white/20 transition">Home</Link>
                        <Link href="/request" className="px-3 py-2 rounded-md hover:bg-white/20 transition">My Requests</Link>
                    </nav>

                    <div className="flex items-center gap-3">
                        {/* Dark/Light Toggle */}
                        <div onClick={toggleDarkMode} className="relative w-16 h-8 rounded-full cursor-pointer transition-all duration-500 bg-gradient-to-r from-yellow-300 to-yellow-400 dark:from-gray-700 dark:to-gray-900 flex items-center p-1">
                            <div className={`absolute top-0.5 left-0.5 w-7 h-7 rounded-full bg-white dark:bg-yellow-400 shadow-lg transform transition-transform duration-500 ${darkMode ? "translate-x-8 rotate-180" : "translate-x-0 rotate-0"}`}>
                                <div className="flex items-center justify-center w-full h-full text-xl">
                                    {darkMode ? "🌙" : "☀️"}
                                </div>
                            </div>
                        </div>

                        {/* Notifications */}
                        {/* Notifications */}
                        <div className="relative" ref={notificationRef}>
                            {/* Bell icon */}
                            <button
                                onClick={() => setShowNotifications(!showNotifications)}
                                className="relative inline-flex items-center justify-center w-10 h-10 rounded-full bg-gray-200 dark:bg-gray-700 hover:bg-gray-300 dark:hover:bg-gray-600 transition"
                            >
                                <FaBell className="text-gray-700 dark:text-gray-200" size={20} />
                                {notifications.length > 0 && (
                                    <span className="absolute -top-1 -right-1 inline-flex items-center justify-center px-2 py-1 text-xs font-bold leading-none text-white bg-red-600 rounded-full shadow">
                                        {notifications.length}
                                    </span>
                                )}
                            </button>

                            {/* Dropdown */}
                            {showNotifications && (
                                <div className="absolute right-0 mt-2 w-80 bg-white dark:bg-gray-800 shadow-lg rounded-lg overflow-hidden z-50 border border-gray-200 dark:border-gray-700">
                                    {/* Header with Clear All */}
                                    <div className="flex justify-between items-center px-4 py-2 border-b border-gray-200 dark:border-gray-700">
                                        <span className="font-semibold text-sm text-gray-700 dark:text-gray-200">Notifications</span>
                                        {notifications.length > 0 && (
                                            <button
                                                onClick={clearAll}
                                                className="text-xs text-blue-500 hover:underline focus:outline-none"
                                            >
                                                Clear All
                                            </button>
                                        )}
                                    </div>

                                    {/* Notification items */}
                                    <div className="max-h-64 overflow-y-auto">
                                        {notifications.length === 0 ? (
                                            <div className="p-4 text-sm text-gray-500 dark:text-gray-300">
                                                No notifications
                                            </div>
                                        ) : (
                                            notifications.map((n) => (
                                                <div
                                                    key={n.id}
                                                    className="px-4 py-3 border-b border-gray-200 dark:border-gray-700 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700 transition"
                                                >
                                                    <p className="text-sm text-gray-700 dark:text-gray-200">{n.message}</p>
                                                    <span className="text-xs text-gray-400 dark:text-gray-400">{new Date(n.createdAt).toLocaleString()}</span>
                                                </div>
                                            ))
                                        )}
                                    </div>
                                </div>
                            )}
                        </div>


                        {user ? (
                            <div className="hidden md:flex items-center gap-3">
                                <div className="w-10 h-10 rounded-full bg-purple-500 flex items-center justify-center text-white font-bold text-lg">{avatarLetter}</div>
                                <Button variant="destructive" onClick={onLogout}>Logout</Button>
                            </div>
                        ) : (
                            <div className="hidden md:flex gap-3">
                                <Link href="/login"><Button variant="default">Login</Button></Link>
                                <Link href="/register"><Button variant="default">Register</Button></Link>
                            </div>
                        )}

                        <div className="md:hidden flex items-center z-50">
                            <button onClick={() => setMobileOpen(!mobileOpen)}>
                                {mobileOpen ? <HiX size={24} /> : <HiMenu size={24} />}
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            {/* Mobile Dropdown Menu */}
            {mobileOpen && (
                <div
                    className={`absolute top-16 left-0 w-full rounded-b-xl shadow-xl transition-colors duration-300 z-40
                    ${darkMode ? 'bg-gray-800 text-white' : 'bg-purple-100 text-gray-900'}`}
                >
                    <div className="flex flex-col px-4 py-4 gap-2">
                        <Link
                            href="/"
                            className="block py-2 px-3 rounded hover:bg-purple-200 dark:hover:bg-gray-700 transition-colors"
                            onClick={() => setMobileOpen(false)}
                        >
                            Home
                        </Link>
                        {user && (
                            <Link
                                href="/request"
                                className="block py-2 px-3 rounded hover:bg-purple-200 dark:hover:bg-gray-700 transition-colors"
                                onClick={() => setMobileOpen(false)}
                            >
                                My Requests
                            </Link>
                        )}
                        {user ? (
                            <button
                                className="block w-full text-left py-2 px-3 rounded hover:bg-purple-200 dark:hover:bg-gray-700 transition-colors"
                                onClick={onLogout}
                            >
                                Logout
                            </button>
                        ) : (
                            <>
                                <Link
                                    href="/login"
                                    className="block py-2 px-3 rounded hover:bg-purple-200 dark:hover:bg-gray-700 transition-colors"
                                    onClick={() => setMobileOpen(false)}
                                >
                                    Login
                                </Link>
                                <Link
                                    href="/register"
                                    className="block py-2 px-3 rounded hover:bg-purple-200 dark:hover:bg-gray-700 transition-colors"
                                    onClick={() => setMobileOpen(false)}
                                >
                                    Register
                                </Link>
                            </>
                        )}
                    </div>
                </div>
            )}
        </header>
    )
}
