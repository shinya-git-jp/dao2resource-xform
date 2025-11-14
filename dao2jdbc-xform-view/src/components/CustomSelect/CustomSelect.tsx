import {  MenuItem, Select } from "@mui/material";
import CustomLabel from "../CustomLabel";

type CustomSelectItem = {
    value: string;
    label: string;
}
interface CustomSelectProps {
    label?: string;
    useTooltip?: boolean;
    tooltipText?: string;
    value?: string;
    onChange?: (value: string) => void;
    ariaLabel: string;
    options: CustomSelectItem[];
    dataTestId?: string;
    defaultOptions?: boolean;
    defaultOptionText?: string;
    hideLabel?: boolean;
}

const CustomSelect: React.FC<CustomSelectProps> = ({
    label = "",
    useTooltip = false,
    tooltipText = "Select an option",
    value = "",
    onChange = () => {},
    ariaLabel,
    options = [],
    dataTestId = "",
    defaultOptions = false,
    defaultOptionText = "選択してください",
    hideLabel = false,
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
            <Select
            size="small"
            aria-label={ariaLabel}
            fullWidth={true}
            displayEmpty={defaultOptions}
            data-testid={dataTestId}
            value={value}
            onChange={(e) => onChange(e.target.value)}>
                {
                    defaultOptions &&
                    <MenuItem value="">
                        <em>{defaultOptionText}</em>
                    </MenuItem>
                }
                {
                    options.map((item) => (
                        <MenuItem key={item.value} value={item.value}>
                            {item.label}
                        </MenuItem>
                    ))
                }
            </Select>
        </div>
    );
}
export default CustomSelect;