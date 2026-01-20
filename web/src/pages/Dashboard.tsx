import React, { useEffect, useState } from 'react';
import { Grid, Typography, Box, CircularProgress } from '@mui/material';
import WbSunnyIcon from '@mui/icons-material/WbSunny';
import RestaurantIcon from '@mui/icons-material/Restaurant';
import LogoutIcon from '@mui/icons-material/Logout';
import SummaryCard from '../components/SummaryCard';

// TODO: Replace with real data from Firestore
const MOCK_STATS = {
    working: 12,
    break: 3,
    left: 45
};

const Dashboard: React.FC = () => {
    const [loading, setLoading] = useState(true);
    const [stats, setStats] = useState(MOCK_STATS);

    useEffect(() => {
        // Simulate data fetching
        const timer = setTimeout(() => {
            setStats(MOCK_STATS);
            setLoading(false);
        }, 1000);
        return () => clearTimeout(timer);
    }, []);

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box>
            <Typography variant="h4" gutterBottom sx={{ mb: 4 }}>
                本日の勤怠状況
            </Typography>
            <Grid container spacing={3}>
                <Grid size={{ xs: 12, sm: 4 }}>
                    <SummaryCard
                        title="出勤中"
                        count={stats.working}
                        icon={WbSunnyIcon}
                        color="#2e7d32" // Green
                    />
                </Grid>
                <Grid size={{ xs: 12, sm: 4 }}>
                    <SummaryCard
                        title="休憩中"
                        count={stats.break}
                        icon={RestaurantIcon}
                        color="#ed6c02" // Orange
                    />
                </Grid>
                <Grid size={{ xs: 12, sm: 4 }}>
                    <SummaryCard
                        title="退勤済"
                        count={stats.left}
                        icon={LogoutIcon}
                        color="#1976d2" // Blue
                    />
                </Grid>
            </Grid>
        </Box>
    );
};

export default Dashboard;
