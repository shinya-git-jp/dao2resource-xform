import type { Meta, StoryObj } from '@storybook/react';

import CustomLabel from './CustomLabel';

const meta = {
  component: CustomLabel,
} satisfies Meta<typeof CustomLabel>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    useTooltip: true,
    tooltipText: "This is a tooltip",
    label: "Default Label",
  }
};