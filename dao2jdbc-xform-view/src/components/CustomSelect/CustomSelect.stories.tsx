import type { Meta, StoryObj } from '@storybook/react';

import CustomSelect from './CustomSelect';
import React from 'react';
import { render } from 'react-dom';

const meta = {
  component: CustomSelect,
} satisfies Meta<typeof CustomSelect>;

export default meta;

type Story = StoryObj<typeof meta>;

const renderedCustomSelect = (args: any) => {
  const [value, setValue] = React.useState<string>(args.value || "");
  return (
    <CustomSelect
      {...args}
      value={value}
      onChange={(val) => {
        setValue(val);
      }}
    />
  );
}

export const Default: Story = {
  render: renderedCustomSelect,
  args: {
    label: "Default Select",
    useTooltip:  true,
    tooltipText: "This is a tooltip",
    value: "test",
    onChange: ()=> {},
    options: [
      { value: "option1", label: "Option 1" },
      { value: "option2", label: "Option 2" },
      { value: "option3", label: "Option 3" }
    ],
    dataTestId: "test-select",
    defaultOptions:  true,
    defaultOptionText:  "選択してください",
    hideLabel:  false,
    ariaLabel: "default-select"
  }
};