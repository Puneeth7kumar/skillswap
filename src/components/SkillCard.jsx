'use client'

import React from 'react'
import { Button } from '@/components/ui/button'

export default function SkillCard({ skill, onRequest, darkMode }) {
    return (
        <div
            className={`rounded-2xl p-6 shadow-lg transition hover:scale-105 ${darkMode
                ? 'bg-gray-800 text-white shadow-purple-900/50'
                : 'bg-white text-gray-900'
                }`}
        >
            {skill.user && (
                <h2 className="text-sm font-medium text-gray-400 mb-1">
                    added by: <span className="text-purple-500">{skill.user.name}</span>
                </h2>
            )}

            <h3 className="text-xl font-bold mb-2 text-purple-500">{skill.title}</h3>
            <p className="text-gray-500 mb-4">{skill.description}</p>

            <div className="flex justify-between items-center">
                <span className="text-sm bg-purple-900/40 px-3 py-1 rounded-full">
                    {skill.level}
                </span>
                <Button size="sm" onClick={() => onRequest(skill)}>
                    Request
                </Button>
            </div>
        </div>
    )
}
