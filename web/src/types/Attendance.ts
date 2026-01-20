import { Timestamp } from 'firebase/firestore';

export interface AttendanceRecord {
    id: string;
    userId: string;
    userName?: string; // Enhanced with joined user data if needed
    date: string;
    clockIn: Timestamp | null;
    clockOut: Timestamp | null;
    status: 'working' | 'break' | 'left' | 'absent';
    workTimeMinutes: number;
    updatedAt: Timestamp;
}
