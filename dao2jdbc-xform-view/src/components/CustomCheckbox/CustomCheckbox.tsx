import { Checkbox, FormControlLabel, Tooltip } from "@mui/material";
import InfoIcon from '@mui/icons-material/Info';

interface CustomCheckboxProps {
    label?: string;
    useTooltip?: boolean;
    tooltipText?: string;
    checked?: boolean;
    onChange?: (checked: boolean) => void;
    ariaLabel: string;
    dataTestId?: string;
}

const CustomCheckbox: React.FC<CustomCheckboxProps> = ({
    label = "",
    useTooltip = false,
    tooltipText = "Check this box",
    checked = false,
    onChange = () => {},
    ariaLabel,
    dataTestId = ""
}) => {
    return (
        <div style={{display: "flex", alignItems: "center"}}>
            <FormControlLabel
                label={label}
                control={<Checkbox />}
                checked={checked}
                aria-label={ariaLabel}
                data-testid={dataTestId}
                onChange={(e) => onChange((e.target as HTMLInputElement).checked)}
            />
            {
                useTooltip && 
                <Tooltip title={tooltipText} arrow>
                    <InfoIcon fontSize='small' />
                </Tooltip>
            }
        </div>
    );
}

export default CustomCheckbox;