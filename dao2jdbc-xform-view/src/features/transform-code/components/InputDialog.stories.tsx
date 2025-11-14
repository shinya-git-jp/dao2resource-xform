import type { Meta, StoryObj } from '@storybook/react';

import { InputDialog } from './InputDialog';
import { DbType } from '../../../types/enum';
import { expect, screen, userEvent } from '@storybook/test';
import { useSettingStore } from '../../../store/setting-store';

const meta = {
  component: InputDialog,
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
} satisfies Meta<typeof InputDialog>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    open: true,
    initialScript: { 
      id: "",
      description: "",
      type: "",
      crudTypeSelect: false,
      crudTypeDelete: false,
      crudTypeInsert: false,
      crudTypeUpdate: false,
      setting: null
    },
    onSubmit: () => {},
    onClose: () => {},
  },
  name: '初期状態の場合、全てのフィールドが空になりこと。',
  play: async ({ canvasElement }) => {
    const idInput = (screen.getByPlaceholderText("ID") as HTMLInputElement);
    const categoryId = screen.getByRole("radio", { name: "カテゴリID" }) as HTMLInputElement;
    const veId = screen.getByRole("radio", { name: "仮想表ID" }) as HTMLInputElement;
    const selectType = screen.getByRole("checkbox", { name: "SELECT" }) as HTMLInputElement;
    const deleteType = screen.getByRole("checkbox", { name: "DELETE" }) as HTMLInputElement;
    const updateType = screen.getByRole("checkbox", { name: "UPDATE" }) as HTMLInputElement;
    const insertType = screen.getByRole("checkbox", { name: "INSERT" }) as HTMLInputElement;
    await userEvent.click(screen.getByText("設定情報"));
    const selectedSetting = (screen.getByTestId("selected-setting") as HTMLDivElement).children[0];
    

    await expect(idInput.value).toBe("");
    await expect(categoryId.checked).toBe(false);
    await expect(veId.checked).toBe(false);
    await expect(selectType.checked).toBe(false);
    await expect(deleteType.checked).toBe(false);
    await expect(updateType.checked).toBe(false);
    await expect(insertType.checked).toBe(false);
    await expect(selectedSetting.textContent).toBe("設定を選んでください");
  }
};

export const Filled: Story = {
  args: {
    open: true,
    initialScript: { 
      id: "testID",
      description: "testID",
      type: "categoryId",
      crudTypeSelect: true,
      crudTypeDelete: true,
      crudTypeInsert: true,
      crudTypeUpdate: true,
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
    onClose: () => {},
  },
  name: '初期状態の場合、全てのフィールドが空になりこと。',
  play: async ({ canvasElement }) => {
    const idInput = (screen.getByPlaceholderText("ID") as HTMLInputElement);
    const categoryId = screen.getByRole("radio", { name: "カテゴリID" }) as HTMLInputElement;
    const veId = screen.getByRole("radio", { name: "仮想表ID" }) as HTMLInputElement;
    const selectType = screen.getByRole("checkbox", { name: "SELECT" }) as HTMLInputElement;
    const deleteType = screen.getByRole("checkbox", { name: "DELETE" }) as HTMLInputElement;
    const updateType = screen.getByRole("checkbox", { name: "UPDATE" }) as HTMLInputElement;
    const insertType = screen.getByRole("checkbox", { name: "INSERT" }) as HTMLInputElement;
    await userEvent.click(screen.getByText("設定情報"));
    const selectedSetting = (screen.getByTestId("selected-setting") as HTMLDivElement).children[0];
    

    await expect(idInput.value).toBe("testID");
    await expect(categoryId.checked).toBe(true);
    await expect(veId.checked).toBe(false);
    await expect(selectType.checked).toBe(true);
    await expect(deleteType.checked).toBe(true);
    await expect(updateType.checked).toBe(true);
    await expect(insertType.checked).toBe(true);
    await expect(selectedSetting.textContent).toBe("testSetting");
  }
};