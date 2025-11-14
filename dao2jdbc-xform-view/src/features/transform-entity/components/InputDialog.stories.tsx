import type { Meta, StoryObj } from '@storybook/react';

import { EntityInputDialog } from './InputDialog';
import { v4 as uuidv4 } from 'uuid';
import { expect, screen, userEvent } from '@storybook/test';
import { useSettingStore } from '../../../store/setting-store';
import { DbType } from '../../../types/enum';

const meta = {
  component: EntityInputDialog,
  
    async beforeEach() {
      useSettingStore.getState().addSetting({
        id: "test",
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
    }
} satisfies Meta<typeof EntityInputDialog>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    open: true,
    initialScript: {
      id: uuidv4(), 
      description: "",
      type: "",
      setting: null
    },
    onSubmit: () => {},
    onClose: () => {}
  },
  name: '初期状態の場合、全てのフィールドが空になりこと。',
  play: async ({}) => {
    const idInput = (screen.getByPlaceholderText("ID") as HTMLInputElement);
    const databaseId = screen.getByRole("radio", { name: "データベースID" }) as HTMLInputElement;
    const entityId = screen.getByRole("radio", { name: "エンティティID" }) as HTMLInputElement;
    await userEvent.click(screen.getByText("設定情報"));
    const selectedSetting = (screen.getByTestId("selected-setting") as HTMLDivElement).children[0];
    
    await expect(idInput.value).toBe("");
    await expect(databaseId.checked).toBe(false);
    await expect(entityId.checked).toBe(false);
    await expect(selectedSetting.textContent).toBe("設定を選んでください");
  }
};


export const Filled: Story = {
  args: {
    open: true,
    initialScript: {
      id: uuidv4(), 
      description: "testId",
      type: "databaseId",
      setting: {
        id: "test",
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
      }
    },
    onSubmit: () => {},
    onClose: () => {}
  },
  name: '初期状態の場合、全てのフィールドが空になりこと。',
  play: async ({}) => {
    const idInput = (screen.getByPlaceholderText("ID") as HTMLInputElement);
    const databaseId = screen.getByRole("radio", { name: "データベースID" }) as HTMLInputElement;
    const entityId = screen.getByRole("radio", { name: "エンティティID" }) as HTMLInputElement;
    await userEvent.click(screen.getByText("設定情報"));
    const selectedSetting = (screen.getByTestId("selected-setting") as HTMLDivElement).children[0];
    
    await expect(idInput.value).toBe("testId");
    await expect(databaseId.checked).toBe(true);
    await expect(entityId.checked).toBe(false);
    await expect(selectedSetting.textContent).toBe("testSetting");
  }
};