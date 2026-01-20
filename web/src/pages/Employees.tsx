import React, { useEffect, useState } from 'react';
import {
    Box,
    Typography,
    Button,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    CircularProgress,
    Chip,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import { collection, getDocs, query, orderBy } from 'firebase/firestore';
import { db } from '../firebase/config';
import { type User } from '../types/User';
import AddEmployeeDialog from '../components/AddEmployeeDialog';

const Employees: React.FC = () => {
    const [employees, setEmployees] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [openAddDialog, setOpenAddDialog] = useState(false);

    const fetchEmployees = async () => {
        setLoading(true);
        try {
            const q = query(collection(db, 'users'), orderBy('createdAt', 'desc'));
            const querySnapshot = await getDocs(q);
            const loadedEmployees: User[] = [];
            querySnapshot.forEach((doc) => {
                const data = doc.data();
                loadedEmployees.push({
                    uid: doc.id,
                    email: data.email,
                    displayName: data.displayName,
                    role: data.role,
                    isActive: data.isActive,
                    createdAt: data.createdAt?.toDate().toISOString() || '',
                });
            });
            setEmployees(loadedEmployees);
        } catch (error) {
            console.error('Error fetching employees:', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchEmployees();
    }, []);

    const handleAddSuccess = () => {
        fetchEmployees();
    };

    return (
        <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h4">従業員管理</Typography>
                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={() => setOpenAddDialog(true)}
                >
                    従業員を追加
                </Button>
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
                                <TableCell>氏名</TableCell>
                                <TableCell>メールアドレス</TableCell>
                                <TableCell>権限</TableCell>
                                <TableCell>ステータス</TableCell>
                                <TableCell>登録日</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {employees.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={5} align="center">
                                        従業員が登録されていません。
                                    </TableCell>
                                </TableRow>
                            ) : (
                                employees.map((employee) => (
                                    <TableRow key={employee.uid}>
                                        <TableCell>{employee.displayName}</TableCell>
                                        <TableCell>{employee.email}</TableCell>
                                        <TableCell>
                                            <Chip
                                                label={employee.role === 'admin' ? '管理者' : '従業員'}
                                                color={employee.role === 'admin' ? 'primary' : 'default'}
                                                size="small"
                                            />
                                        </TableCell>
                                        <TableCell>
                                            <Chip
                                                label={employee.isActive ? '有効' : '無効'}
                                                color={employee.isActive ? 'success' : 'error'}
                                                size="small"
                                                variant="outlined"
                                            />
                                        </TableCell>
                                        <TableCell>
                                            {employee.createdAt ? new Date(employee.createdAt).toLocaleDateString() : '-'}
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}

            <AddEmployeeDialog
                open={openAddDialog}
                onClose={() => setOpenAddDialog(false)}
                onSuccess={handleAddSuccess}
            />
        </Box>
    );
};

export default Employees;
