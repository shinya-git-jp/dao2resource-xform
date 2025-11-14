import type { Meta, StoryObj } from '@storybook/react';

import TransformDialog from './TransformDialog';
import { useSettingStore } from '../../../store/setting-store';
import { v4 as uuidv4 } from 'uuid';
import { DbType } from '../../../types/enum';
import { useInputStore } from '../../../store/input-store';
import { expect, screen, userEvent, waitFor } from '@storybook/test';

const meta = {
  component: TransformDialog,
  async beforeEach() {
    useSettingStore.getState().setGlobalSetting({
      id: uuidv4(),
      savedName: "testSetting",
      db: {
        url: "testUrl",
        dbType: DbType.ORACLE,
        schema: "testSchema",
        username: "testUsername",
        password: "testPassword"
      },
      locale: {
      current: "ja",
      mapping: {
          country1: "country1",
          country2: "country2",
          country3: "country3",
          country4: "country4",
          country5: "country5"
      }
      },
      reservedWords: {
        primaryKey: "primaryKey",
        exclusiveKey: "exclusiveKey",
        companyCodeKey: "companyCodeKey",
        insertedUserIDKey: "insertedUserIDKey",
        insertedDateKey: "insertedDateKey",
        updatedUserIDKey: "updatedUserIDKey",
        updatedDateKey: "updatedDateKey"
      },
      transform: {
        encoding: "UTF-8",
        forceAliasColumn: false,
        entityQuery: true,
        useForeignKey: false,
        useExpMap: false
      },
      savedTime: ""
    });
    useInputStore.getState().resetScripts();
  }
} satisfies Meta<typeof TransformDialog>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    scripts: []
  },
  name: '初期状態',
  play: async ({}) => {
    expect(screen.getByText("0 / 0ページ（0-0件 / 0件）")).toBeVisible();
    expect(screen.getByRole("button", {name: "前へ"})).toBeDisabled();
    expect(screen.getByRole("button", {name: "次へ"})).toBeDisabled();
    const transformBtn = screen.getByText("変換");
    expect(transformBtn).toBeVisible();
    expect(transformBtn).toBeEnabled();
    expect(screen.getByText("処理中...")).toBeVisible();
  }
};

export const TestShowData: Story = {
  args: {
    scripts: [
      {
        id: "0530ABBD2F0A4A0F8D7E6F58776897F7",
        description: "0530ABBD2F0A4A0F8D7E6F58776897F7",
        type: "veId",
        crudTypeSelect: true,
        crudTypeDelete: false,
        crudTypeInsert: false,
        crudTypeUpdate: false,
        setting: null
      },
      
      {
        id: "0530ABBD2F0A4A0F8D7E6F58776897F1",
        description: "0530ABBD2F0A4A0F8D7E6F58776897F1",
        type: "veId",
        crudTypeSelect: true,
        crudTypeDelete: false,
        crudTypeInsert: false,
        crudTypeUpdate: false,
        setting: null
      },
      {
        id: "0530ABBD2F0A4A0F8D7E6F58776897F2",
        description: "0530ABBD2F0A4A0F8D7E6F58776897F2",
        type: "veId",
        crudTypeSelect: true,
        crudTypeDelete: false,
        crudTypeInsert: false,
        crudTypeUpdate: false,
        setting: null
      },
      {
        id: "0530ABBD2F0A4A0F8D7E6F58776897F3",
        description: "0530ABBD2F0A4A0F8D7E6F58776897F3",
        type: "veId",
        crudTypeSelect: true,
        crudTypeDelete: false,
        crudTypeInsert: false,
        crudTypeUpdate: false,
        setting: null
      },
      {
        id: "0530ABBD2F0A4A0F8D7E6F58776897F4",
        description: "0530ABBD2F0A4A0F8D7E6F58776897F4",
        type: "veId",
        crudTypeSelect: true,
        crudTypeDelete: false,
        crudTypeInsert: false,
        crudTypeUpdate: false,
        setting: null
      },
      {
        id: "0530ABBD2F0A4A0F8D7E6F58776897F5",
        description: "0530ABBD2F0A4A0F8D7E6F58776897F5",
        type: "veId",
        crudTypeSelect: true,
        crudTypeDelete: false,
        crudTypeInsert: false,
        crudTypeUpdate: false,
        setting: null
      },
      {
        id: "0530ABBD2F0A4A0F8D7E6F58776897F6",
        description: "0530ABBD2F0A4A0F8D7E6F58776897F6",
        type: "veId",
        crudTypeSelect: true,
        crudTypeDelete: false,
        crudTypeInsert: false,
        crudTypeUpdate: false,
        setting: null
      },
      {
        id: "0530ABBD2F0A4A0F8D7E6F58776897F8",
        description: "0530ABBD2F0A4A0F8D7E6F58776897F8",
        type: "veId",
        crudTypeSelect: true,
        crudTypeDelete: false,
        crudTypeInsert: false,
        crudTypeUpdate: false,
        setting: null
      },
      {
        id: "0530ABBD2F0A4A0F8D7E6F58776897F9",
        description: "0530ABBD2F0A4A0F8D7E6F58776897F9",
        type: "veId",
        crudTypeSelect: true,
        crudTypeDelete: false,
        crudTypeInsert: false,
        crudTypeUpdate: false,
        setting: null
      },
      {
        id: "0530ABBD2F0A4A0F8D7E6F58776897F0",
        description: "0530ABBD2F0A4A0F8D7E6F58776897F0",
        type: "veId",
        crudTypeSelect: true,
        crudTypeDelete: false,
        crudTypeInsert: false,
        crudTypeUpdate: false,
        setting: null
      },
      {
        id: "0530ABBD2F0A4A0F8D7E6F5877689711",
        description: "0530ABBD2F0A4A0F8D7E6F5877689711",
        type: "veId",
        crudTypeSelect: true,
        crudTypeDelete: false,
        crudTypeInsert: false,
        crudTypeUpdate: false,
        setting: null
      }
    ]
  },
  name: 'データが存在する場合、表示されること。',
  play: async ({}) => {
    let prevBtn = screen.getByRole("button", {name: "前へ"});
    let nextBtn = screen.getByRole("button", {name: "次へ"});
    expect(screen.getByText("1 / 2ページ（1-10件 / 11件）")).toBeVisible();
    expect(prevBtn).toBeDisabled();
    expect(nextBtn).toBeEnabled();
  }
};
