import type { Meta, StoryObj } from '@storybook/react';

import CustomRadioGroup from './CustomRadioGroup';
import React from 'react';

const meta = {
  component: CustomRadioGroup,
} satisfies Meta<typeof CustomRadioGroup>;

export default meta;

type Story = StoryObj<typeof meta>;

const renderedCustomRadioGroup = (args: any) => {
  const [value, setValue] = React.useState<string>(args.value || "");
  return (
    <CustomRadioGroup
      {...args}
      value={value}
      onChange={(val) => {
        setValue(val);
      }}
    />
  );
}

export const Default: Story = {
  render: renderedCustomRadioGroup,
  args: {
    label: "Default Radio Group",
    useTooltip: true,
    tooltipText: "This is a tooltip",
    value: "test",
    onChange: ()=> {},
    items: [
      { value: "option1", label: "Option 1" },
      { value: "option2", label: "Option 2" },
      { value: "option3", label: "Option 3" }
    ],
    dataTestId: "test-radio-group",
    hideLabel: false,
    ariaLabel: "default-radio-group"
  }
};