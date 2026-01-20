import React, { useState, useEffect } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Button,
    Alert,
} from '@mui/material';
import { doc, updateDoc, Timestamp, serverTimestamp } from 'firebase/firestore';
import { db } from '../firebase/config';
import { type AttendanceRecord } from '../types/Attendance';
import { parse, format } from 'date-fns';

interface EditAttendanceDialogProps {
    open: boolean;
    onClose: () => void;
    record: AttendanceRecord | null;
    onSuccess: () => void;
}

const EditAttendanceDialog: React.FC<EditAttendanceDialogProps> = ({ open, onClose, record, onSuccess }) => {
    const [clockInTime, setClockInTime] = useState('');
    const [clockOutTime, setClockOutTime] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (record) {
            setClockInTime(record.clockIn ? format(record.clockIn.toDate(), 'HH:mm') : '');
            setClockOutTime(record.clockOut ? format(record.clockOut.toDate(), 'HH:mm') : '');
        } else {
            setClockInTime('');
            setClockOutTime('');
        }
    }, [record]);

    const handleSubmit = async () => {
        if (!record) return;

        setLoading(true);
        setError('');

        try {
            const dateStr = record.date; // YYYY-MM-DD

            let newClockIn: Timestamp | null = null;
            let newClockOut: Timestamp | null = null;

            if (clockInTime) {
                const parsedIn = parse(`${dateStr} ${clockInTime}`, 'yyyy-MM-dd HH:mm', new Date());
                newClockIn = Timestamp.fromDate(parsedIn);
            }

            if (clockOutTime) {
                const parsedOut = parse(`${dateStr} ${clockOutTime}`, 'yyyy-MM-dd HH:mm', new Date());
                newClockOut = Timestamp.fromDate(parsedOut);
            }

            if (newClockIn && newClockOut && newClockIn.toMillis() > newClockOut.toMillis()) {
                setError('退勤時間は出勤時間より後である必要があります。');
                setLoading(false);
                return;
            }

            // Calculate work time (simplified)
            let workTimeMinutes = 0;
            if (newClockIn && newClockOut) {
                const diffMs = newClockOut.toMillis() - newClockIn.toMillis();
                workTimeMinutes = Math.floor(diffMs / (1000 * 60));
            }

            await updateDoc(doc(db, 'attendance_records', record.id), {
                clockIn: newClockIn,
                clockOut: newClockOut,
                workTimeMinutes: workTimeMinutes,
                updatedAt: serverTimestamp(),
                status: newClockOut ? 'left' : 'working' // Simple status update
            });

            onSuccess();
            onClose();
        } catch (err) {
            console.error(err);
            setError('勤怠の修正に失敗しました。');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>勤怠修正: {record?.date} ({record?.userName})</DialogTitle>
            <DialogContent>
                {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
                <TextField
                    margin="dense"
                    label="出勤時間"
                    type="time"
                    fullWidth
                    variant="outlined"
                    InputLabelProps={{ shrink: true }}
                    inputProps={{ step: 300 }} // 5 min
                    value={clockInTime}
                    onChange={(e) => setClockInTime(e.target.value)}
                />
                <TextField
                    margin="dense"
                    label="退勤時間"
                    type="time"
                    fullWidth
                    variant="outlined"
                    InputLabelProps={{ shrink: true }}
                    inputProps={{ step: 300 }} // 5 min
                    value={clockOutTime}
                    onChange={(e) => setClockOutTime(e.target.value)}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} disabled={loading}>キャンセル</Button>
                <Button onClick={handleSubmit} variant="contained" disabled={loading}>
                    保存
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default EditAttendanceDialog;
