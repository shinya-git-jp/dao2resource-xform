import { FormControlLabel, Radio, RadioGroup } from "@mui/material";
import CustomLabel from "../CustomLabel";

type CustomRadioItem = {
    value: string;
    label: string;
}
interface CustomRadioGroupProps {
    label?: string;
    useTooltip?: boolean;
    tooltipText?: string;
    value?: string;
    onChange?: (value: string) => void;
    ariaLabel: string;
    items: CustomRadioItem[];
    dataTestId?: string;
    hideLabel?: boolean;
}
const CustomRadioGroup : React.FC<CustomRadioGroupProps> = ({
    label = "",
    useTooltip = false,
    tooltipText = "Select an option",
    value = "",
    onChange = () => {},
    ariaLabel,
    items = [],
    dataTestId = "",
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
            <RadioGroup
                aria-label={ariaLabel}
                data-testid={dataTestId}
                value={value}
                onChange={(e) => onChange(e.target.value)}
                row
            >
                {
                    items.map((item) => (
                        <FormControlLabel
                            key={item.value}
                            value={item.value}
                            control={<Radio />}
                            label={item.label}
                        />
                    ))
                }
                
            </RadioGroup>
        </div>
    );
}
export default CustomRadioGroup;