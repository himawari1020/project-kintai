export interface User {
    uid: string;
    email: string;
    displayName: string;
    role: 'admin' | 'employee';
    isActive: boolean;
    createdAt: string; // ISO string for simplicity in frontend
}
