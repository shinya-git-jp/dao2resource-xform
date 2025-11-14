import type { Meta, StoryObj } from '@storybook/react';

import DashboardPage from './DashboardPage';
import { expect, within } from '@storybook/test';

const meta = {
  component: DashboardPage,
  tags: ['autodocs'],
} satisfies Meta<typeof DashboardPage>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    pathname: null
  },
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
    const title = canvas.getByText("仮想表転換の条件一覧");
    await expect(title).toBeVisible();
  }
};

export const Settings: Story = {
  args: {
    pathname : "/settings"
  },
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
    const title = canvas.getByText("設定一覧");
    await expect(title).toBeVisible();
  }
}

export const TransformCode: Story = {
  args: {
    pathname: "/transform/code"
  },
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
    const title = canvas.getByText("仮想表転換の条件一覧");
    await expect(title).toBeVisible();
  }
}

export const TransformEntity: Story = {
  args: {
    pathname: "/transform/entity"
  },
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
    const title = canvas.getByText("エンティティ転換の条件一覧");
    await expect(title).toBeVisible();
  }
}