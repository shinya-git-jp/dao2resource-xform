import { TextField } from "@mui/material";
import CustomLabel from "../CustomLabel/CustomLabel";

interface CustomTextFieldProps {
    label?: string;
    useTooltip?: boolean;
    tooltipText?: string;
    value?: string;
    onChange?: (value: string) => void;
    ariaLabel: string;
    placeholder?: string;
    dataTestId?: string;
    hideLabel?: boolean;
    multiline?: boolean;
}
const CustomTextField : React.FC<CustomTextFieldProps> = ({
    label = "",
    useTooltip = false,
    tooltipText = "Enter text",
    value = "",
    onChange = () => {},
    ariaLabel,
    placeholder = "",
    dataTestId = "",
    hideLabel = false,
    multiline = false,
}) => {
    return (
        <div>
            {!hideLabel &&
            <CustomLabel
                label={label}
                useTooltip={useTooltip}
                tooltipText={tooltipText}
            />
            }
            <TextField
                required
                size="small"
                multiline={multiline}
                placeholder={placeholder}
                aria-label={ariaLabel}
                fullWidth={true}
                value={value}
                data-testid={dataTestId}
                onChange={(e) => onChange(e.target.value)}
            />
        </div>
    )
}

export default CustomTextField;