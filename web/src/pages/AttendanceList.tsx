import React, { useEffect, useState } from 'react';
import {
    Box,
    Typography,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    CircularProgress,
    Button,
    IconButton,
    TextField,
    Chip,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DownloadIcon from '@mui/icons-material/Download';
import { collection, getDocs, query, where, orderBy, getDoc, doc } from 'firebase/firestore';
import { db } from '../firebase/config';
import { type AttendanceRecord } from '../types/Attendance';
import EditAttendanceDialog from '../components/EditAttendanceDialog';
import { format } from 'date-fns';

const AttendanceList: React.FC = () => {
    // Current month as default (YYYY-MM)
    const [selectedMonth, setSelectedMonth] = useState(format(new Date(), 'yyyy-MM'));
    const [records, setRecords] = useState<AttendanceRecord[]>([]);
    const [loading, setLoading] = useState(false);

    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [selectedRecord, setSelectedRecord] = useState<AttendanceRecord | null>(null);

    const fetchRecords = async () => {
        setLoading(true);
        try {
            // Simplified query: In production, better to use startAt/endAt timestamps on 'date' or separate year/month fields
            // For this demo, we assume 'date' string field format "YYYY-MM-DD" and do client-side filtering or
            // simple string range query if possible. Here: just range query on date string.

            const startStr = `${selectedMonth}-01`;
            const endStr = `${selectedMonth}-31`;

            const q = query(
                collection(db, 'attendance_records'),
                where('date', '>=', startStr),
                where('date', '<=', endStr),
                orderBy('date', 'desc')
            );

            const querySnapshot = await getDocs(q);
            const loadedRecords: AttendanceRecord[] = [];

            // Fetch User displayName map to avoid N+1 if possible, or just fetch individually map
            // For simplicity, we fetch user data individually here or assume user details are redundant in record.
            // Let's load detailed user info for better display.

            // Enhancement: Fetch all users first to map IDs to Names
            // const usersSnap = await getDocs(collection(db, 'users'));
            // const userMap = ...

            for (const docSnapshot of querySnapshot.docs) {
                const data = docSnapshot.data();

                // Fetch user name if not present (Naive approach)
                let userName = data.userName || 'Unknown';
                if (!userName || userName === 'Unknown') {
                    const userDoc = await getDoc(doc(db, 'users', data.userId));
                    if (userDoc.exists()) {
                        userName = userDoc.data().displayName;
                    }
                }

                loadedRecords.push({
                    id: docSnapshot.id,
                    userId: data.userId,
                    userName: userName,
                    date: data.date,
                    clockIn: data.clockIn,
                    clockOut: data.clockOut,
                    status: data.status,
                    workTimeMinutes: data.workTimeMinutes,
                    updatedAt: data.updatedAt,
                } as AttendanceRecord);
            }

            setRecords(loadedRecords);

        } catch (error) {
            console.error('Error fetching records:', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchRecords();
    }, [selectedMonth]);

    const handleEditClick = (record: AttendanceRecord) => {
        setSelectedRecord(record);
        setEditDialogOpen(true);
    };

    const handleEditSuccess = () => {
        fetchRecords();
    };

    const handleExportCSV = () => {
        if (records.length === 0) return;

        const header = '日付,氏名,出勤,退勤,実働時間(分),ステータス\n';
        const rows = records.map(r => {
            const clockIn = r.clockIn ? format(r.clockIn.toDate(), 'HH:mm') : '';
            const clockOut = r.clockOut ? format(r.clockOut.toDate(), 'HH:mm') : '';
            return `${r.date},${r.userName},${clockIn},${clockOut},${r.workTimeMinutes},${r.status}`;
        }).join('\n');

        const csvContent = header + rows;
        const blob = new Blob([new Uint8Array([0xEF, 0xBB, 0xBF]), csvContent], { type: 'text/csv;charset=utf-8;' }); // BOM for Excel
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `attendance_${selectedMonth}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    return (
        <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h4">勤怠管理</Typography>
                <Box sx={{ display: 'flex', gap: 2 }}>
                    <TextField
                        type="month"
                        label="対象月"
                        InputLabelProps={{ shrink: true }}
                        value={selectedMonth}
                        onChange={(e) => setSelectedMonth(e.target.value)}
                        size="small"
                    />
                    <Button
                        variant="outlined"
                        startIcon={<DownloadIcon />}
                        onClick={handleExportCSV}
                        disabled={records.length === 0}
                    >
                        CSV出力
                    </Button>
                </Box>
            </Box>

            {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
                    <CircularProgress />
                </Box>
            ) : (
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>日付</TableCell>
                                <TableCell>従業員名</TableCell>
                                <TableCell>出勤</TableCell>
                                <TableCell>退勤</TableCell>
                                <TableCell>実働(分)</TableCell>
                                <TableCell>ステータス</TableCell>
                                <TableCell>操作</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {records.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={7} align="center">
                                        対象月のデータがありません。
                                    </TableCell>
                                </TableRow>
                            ) : (
                                records.map((record) => (
                                    <TableRow key={record.id}>
                                        <TableCell>{record.date}</TableCell>
                                        <TableCell>{record.userName}</TableCell>
                                        <TableCell>
                                            {record.clockIn ? format(record.clockIn.toDate(), 'HH:mm') : '-'}
                                        </TableCell>
                                        <TableCell>
                                            {record.clockOut ? format(record.clockOut.toDate(), 'HH:mm') : '-'}
                                        </TableCell>
                                        <TableCell>{record.workTimeMinutes}</TableCell>
                                        <TableCell>
                                            <Chip
                                                label={record.status}
                                                size="small"
                                                color={record.status === 'working' ? 'success' : 'default'}
                                            />
                                        </TableCell>
                                        <TableCell>
                                            <IconButton size="small" onClick={() => handleEditClick(record)}>
                                                <EditIcon />
                                            </IconButton>
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}

            <EditAttendanceDialog
                open={editDialogOpen}
                onClose={() => setEditDialogOpen(false)}
                record={selectedRecord}
                onSuccess={handleEditSuccess}
            />
        </Box>
    );
};

export default AttendanceList;
