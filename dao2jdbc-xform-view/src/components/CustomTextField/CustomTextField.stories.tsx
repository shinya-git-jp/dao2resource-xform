import type { Meta, StoryObj } from '@storybook/react';

import CustomTextField from './CustomTextField';
import React from 'react';

const meta = {
  component: CustomTextField,
} satisfies Meta<typeof CustomTextField>;

export default meta;

type Story = StoryObj<typeof meta>;

const renderedCustomTextField = (args: any) => {
  const [value, setValue] = React.useState<string>(args.value || "");
  return (
    <CustomTextField
      {...args}
      value={value}
      onChange={(val) => {
        setValue(val);
      }}
    />
  );
}

export const Default: Story = {
  render: renderedCustomTextField,
  args: {
    label: "Default Text Field",
    useTooltip:  true,
    tooltipText: "This is a tooltip",
    value:  "test",
    onChange:  () => {},
    placeholder:  "Enter text here",
    dataTestId:  "test-text-field",
    hideLabel:  false,
    multiline:  false,
    ariaLabel: "default-text-field",
  }
};