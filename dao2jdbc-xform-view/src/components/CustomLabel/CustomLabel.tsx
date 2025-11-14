import { Box, InputLabel, Tooltip, Typography } from "@mui/material";
import InfoIcon from '@mui/icons-material/Info';

interface CustomLabelProps {
    label: string;
    useTooltip?: boolean;
    tooltipText?: string;
    required?: boolean
}

const CustomLabel: React.FC<CustomLabelProps> = ({
    label,
    required = false,
    useTooltip = false,
    tooltipText = "Information about this label",
}) => {
    return (
        <Box display="flex" flexDirection={"column"}>
            <InputLabel sx={{color: "#000000"}} required={required}><b>{label}</b></InputLabel>
            <Typography fontSize={12} sx={{color: "#999797"}}>{tooltipText}</Typography>
        </Box>
    );
}

export default CustomLabel;