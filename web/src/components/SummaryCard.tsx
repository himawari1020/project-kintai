import React from 'react';
import { Card, CardContent, Typography, Box, type SvgIconProps } from '@mui/material';

interface SummaryCardProps {
    title: string;
    count: number;
    icon: React.ComponentType<SvgIconProps>;
    color: string;
}

const SummaryCard: React.FC<SummaryCardProps> = ({ title, count, icon: Icon, color }) => {
    return (
        <Card sx={{ height: '100%' }}>
            <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <Box
                        sx={{
                            backgroundColor: `${color}20`, // 20% opacity
                            borderRadius: '50%',
                            p: 1,
                            mr: 2,
                            display: 'flex',
                        }}
                    >
                        <Icon sx={{ color: color }} />
                    </Box>
                    <Typography color="textSecondary" variant="h6" component="div">
                        {title}
                    </Typography>
                </Box>
                <Typography variant="h4" component="div" sx={{ fontWeight: 'bold' }}>
                    {count}
                    <Typography component="span" variant="body1" color="textSecondary" sx={{ ml: 1 }}>
                        名
                    </Typography>
                </Typography>
            </CardContent>
        </Card>
    );
};

export default SummaryCard;
