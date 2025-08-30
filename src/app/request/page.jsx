"use client"

import React, { useEffect, useState } from "react"
import Navbar from "@/components/Navbar"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import Link from "next/link"
import toast, { Toaster } from "react-hot-toast"

export default function MyRequestsPage() {
    const [user, setUser] = useState(null)
    const [madeRequests, setMadeRequests] = useState([])
    const [receivedRequests, setReceivedRequests] = useState([])
    const [darkMode, setDarkMode] = useState(false)


    const toggleDarkMode = () => {
        setDarkMode(prev => {
            localStorage.setItem("darkMode", JSON.stringify(!prev))
            return !prev
        })
    }
    useEffect(() => {
        const savedDarkMode = localStorage.getItem("darkMode")
        if (savedDarkMode !== null) setDarkMode(JSON.parse(savedDarkMode))
        const savedUser = localStorage.getItem("user")
        if (savedUser) setUser(JSON.parse(savedUser))

        const token = localStorage.getItem("token")
        if (!token) return

        fetch("https://skillswap-2-z66g.onrender.com/api/requests/made", {
            headers: { Authorization: `Bearer ${token}` },
        })
            .then((res) => res.json())
            .then(setMadeRequests)

        fetch("https://skillswap-2-z66g.onrender.com/api/requests/received", {
            headers: { Authorization: `Bearer ${token}` },
        })
            .then((res) => res.json())
            .then(setReceivedRequests)
    }, [])

    const handleLogout = () => {
        localStorage.removeItem("token")
        localStorage.removeItem("user")
        setUser(null)
    }

    const updateStatus = async (id, status) => {
        const token = localStorage.getItem("token")
        try {
            const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/requests/${id}/status`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({ status }),
            })
            const updatedRequest = await res.json()
            if (!res.ok) throw new Error(updatedRequest.message || "Failed to update status")

            setMadeRequests((prev) => prev.map((r) => (r.id === id ? updatedRequest : r)))
            setReceivedRequests((prev) => prev.map((r) => (r.id === id ? updatedRequest : r)))
            toast.success(`Status Updated Successfully`)
        } catch (err) {
            console.error(err)
            alert(err.message)
        }
    }

    const getStatusBadge = (status) => {
        const colors = {
            PENDING: "bg-yellow-500/20 text-yellow-400",
            ACCEPTED: "bg-green-500/20 text-green-400",
            REJECTED: "bg-red-500/20 text-red-400",
            COMPLETED: "bg-gray-500/20 text-gray-400",
        }
        return <Badge className={`${colors[status]} px-3 py-1 rounded-full`}>{status}</Badge>
    }

    const RequestCard = ({ request, type }) => (
        <Card
            className={`rounded-2xl shadow-lg hover:shadow-purple-500/40 transform transition-transform hover:scale-105 ${darkMode ? "bg-gray-800 border-gray-700" : "bg-white border-gray-200"
                }`}
        >
            <CardHeader>
                <CardTitle className={darkMode ? "text-purple-300" : "text-purple-500"}>
                    {request.skill?.title}
                </CardTitle>
            </CardHeader>
            <CardContent>
                <p className={darkMode ? "text-gray-300" : "text-gray-700"} text-sm>
                    {type === "made"
                        ? `Requested from: ${request.skill.user?.name}`
                        : `Requested by: ${request.requester?.name}`}
                </p>

                <div className="flex items-center justify-between mt-3">{getStatusBadge(request.status)}</div>

                {type === "received" && request.status === "PENDING" && (
                    <div className="mt-4 flex gap-3">
                        <Button
                            className="bg-green-600 hover:bg-green-700 text-white rounded-xl"
                            onClick={() => updateStatus(request.id, "ACCEPTED")}
                        >
                            Accept
                        </Button>
                        <Button variant="destructive" className="rounded-xl" onClick={() => updateStatus(request.id, "REJECTED")}>
                            Reject
                        </Button>
                    </div>
                )}

                {type === "made" && request.status === "ACCEPTED" && (
                    <div className="mt-4">
                        <Button
                            className="bg-blue-600 hover:bg-blue-700 text-white rounded-xl"
                            onClick={() => updateStatus(request.id, "COMPLETED")}
                        >
                            Mark as Completed
                        </Button>
                    </div>
                )}
            </CardContent>
        </Card>
    )

    return (
        <div className={darkMode ? "dark" : ""}>
            <div
                className={`min-h-screen transition-colors duration-500 ${darkMode
                    ? "bg-gray-900 text-white"
                    : "bg-gradient-to-b from-gray-200 via-indigo-200 to-purple-200 text-gray-900"
                    }`}
            >
                <Toaster position="top-right" reverseOrder={false} />
                {/* Navbar */}
                <Navbar user={user} onLogout={handleLogout} darkMode={darkMode} toggleDarkMode={toggleDarkMode} />

                <main className="max-w-7xl mx-auto px-6 py-10">
                    <div className="mb-10 flex justify-between items-center">
                        <h1 className={`text-4xl font-extrabold ${darkMode ? "text-purple-300" : "text-purple-500"}`}>
                            My Requests
                        </h1>
                        <Link href="/">
                            <Button
                                variant="outline"
                                className={`rounded-full shadow-md px-4 py-2 transition-colors ${darkMode
                                    ? "bg-gray-800 text-white border-gray-600 hover:bg-gray-700 hover:border-purple-500"
                                    : "bg-white text-gray-800 border-gray-300 hover:bg-gray-100 hover:border-purple-400"
                                    }`}
                            >
                                ⬅ Back to Home
                            </Button>
                        </Link>
                    </div>

                    <div className="grid md:grid-cols-2 gap-10">
                        {/* Requests I Made */}
                        <div>
                            <h2 className={`text-2xl font-semibold mb-4 ${darkMode ? "text-purple-300" : "text-purple-500"}`}>
                                Requests I Made
                            </h2>
                            {madeRequests.length > 0 ? (
                                <div className="space-y-4">
                                    {madeRequests.map((r) => (
                                        <RequestCard key={r.id} request={r} type="made" />
                                    ))}
                                </div>
                            ) : (
                                <p className={darkMode ? "text-gray-400 italic" : "text-gray-600 italic"}>No requests made yet.</p>
                            )}
                        </div>

                        {/* Requests I Received */}
                        <div>
                            <h2 className={`text-2xl font-semibold mb-4 ${darkMode ? "text-purple-300" : "text-purple-500"}`}>
                                Requests I Received
                            </h2>
                            {receivedRequests.length > 0 ? (
                                <div className="space-y-4">
                                    {receivedRequests.map((r) => (
                                        <RequestCard key={r.id} request={r} type="received" />
                                    ))}
                                </div>
                            ) : (
                                <p className={darkMode ? "text-gray-400 italic" : "text-gray-600 italic"}>No requests received yet.</p>
                            )}
                        </div>
                    </div>
                </main>

                <footer className={`text-center py-6 border-t transition-colors duration-300 ${darkMode ? "text-gray-400 border-gray-700" : "text-gray-600 border-gray-300"
                    }`}>
                    © {new Date().getFullYear()} SkillSwap Hub — Built with ❤️
                </footer>
            </div>
        </div>
    )
}
