import type { Meta, StoryObj } from '@storybook/react';

import CustomCheckbox from './CustomCheckbox';
import React from 'react';

const meta = {
  component: CustomCheckbox,
} satisfies Meta<typeof CustomCheckbox>;

export default meta;

type Story = StoryObj<typeof meta>;

const renderedCustomCheckbox = (args: any) => {
  const [checked, setChecked] = React.useState<boolean>(args.checked || false);
  return (
    <CustomCheckbox
      {...args}
      checked={checked}
      onChange={(val) => {
        setChecked(val);
      }}
    />
  );
}

export const Default: Story = {
  render: renderedCustomCheckbox,
  args: {
    label: "Default Checkbox",
    useTooltip: true,
    tooltipText: "This is a tooltip",
    checked: true,
    onChange: ()=> {},
    dataTestId: "test-checkbox",
    ariaLabel: "default-checkbox"
  }
};