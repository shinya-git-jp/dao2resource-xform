import type { Meta, StoryObj } from '@storybook/react';

import InputList from './InputList';
import { expect, screen, userEvent, waitFor, within } from '@storybook/test';
import { useSettingStore } from '../../../store/setting-store';
import { v4 as uuidv4 } from 'uuid';
import { DbType } from '../../../types/enum';
import { useInputStore } from '../../../store/input-store';

const meta = {
  component: InputList,
  async beforeEach() {
    useSettingStore.getState().resetSettings();
    useInputStore.getState().resetScripts();
    useSettingStore.getState().addSetting({
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
  }
} satisfies Meta<typeof InputList>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {},
  name: '初期状態の場合、全てのフィールドが空になりこと。',
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
    
    await expect(canvas.getByTestId("empty-input-image")).toBeVisible();
  }
};

export const ExpandedGlobalSetting: Story = {
  args: {},
  name: 'グローバル設定が展開されている場合、グローバル設定のフィールドが表示されること。',
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
    await screen.getByText("データベース設定").click();
    await screen.getByText("言語設定").click();
    await screen.getByText("予約語設定").click();
    await screen.getByText("アウトプット設定").click();
    waitFor(() => {
      expect(screen.getByText("URL：None")).toBeVisible();
      expect(screen.getByText("スキーマ：None")).toBeVisible();
      expect(screen.getByText("データベースタイプ：None")).toBeVisible();
      expect(screen.getByText("ユーザ名：None")).toBeVisible();
      expect(screen.getByText("パスワード：None")).toBeVisible();
      expect(screen.getByText("使用する言語：None")).toBeVisible();
      expect(screen.getByText("country1の言語：None")).toBeVisible();
      expect(screen.getByText("country2の言語：None")).toBeVisible();
      expect(screen.getByText("country3の言語：None")).toBeVisible();
      expect(screen.getByText("country4の言語：None")).toBeVisible();
      expect(screen.getByText("country5の言語：None")).toBeVisible();
      expect(screen.getByText("Primaryキーカラム：None")).toBeVisible();
      expect(screen.getByText("Exclusiveキーカラム：None")).toBeVisible();
      expect(screen.getByText("会社コードカラム：None")).toBeVisible();
      expect(screen.getByText("登録者IDカラム：None")).toBeVisible();
      expect(screen.getByText("登録日付カラム：None")).toBeVisible();
      expect(screen.getByText("更新者IDカラム：None")).toBeVisible();
      expect(screen.getByText("更新日付カラム：None")).toBeVisible();
      expect(screen.getByText("ダウンロードされたファイルのエンコーディング：None")).toBeVisible();
      expect(screen.getByText("SQL処理にエンティティ定義情報を使用する：None")).toBeVisible();
      expect(screen.getByText("項目のエイリアスを仮想項目名に上書きする：None")).toBeVisible();
      expect(screen.getByText("引数の値をバインドする際に配列を使用するかどうか：None")).toBeVisible();
      expect(screen.getByText("エンティティ定義の外部キーを使用する：None")).toBeVisible();
    });
    
  }
}


export const SelectGlobalSetting: Story = {
  args: {},
  name: 'グローバル設定を選択され、表示されたグロバル設定の詳細がひょうじされること。',
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
    const globalSetting = (screen.getByTestId("global-setting-select") as HTMLDivElement).children[0];
    await userEvent.click(globalSetting);
    await userEvent.click(screen.getByText("testSetting"));
    await screen.getByText("データベース設定").click();
    await screen.getByText("言語設定").click();
    await screen.getByText("予約語設定").click();
    await screen.getByText("アウトプット設定").click();
    waitFor(() => {
      expect(screen.getByText("URL：testUrl")).toBeVisible();
      expect(screen.getByText("スキーマ：testSchema")).toBeVisible();
      expect(screen.getByText("データベースタイプ：oracle")).toBeVisible();
      expect(screen.getByText("ユーザ名：testUsername")).toBeVisible();
      expect(screen.getByText("パスワード：testPassword")).toBeVisible();
      expect(screen.getByText("使用する言語：ja")).toBeVisible();
      expect(screen.getByText("country1の言語：country1")).toBeVisible();
      expect(screen.getByText("country2の言語：country2")).toBeVisible();
      expect(screen.getByText("country3の言語：country3")).toBeVisible();
      expect(screen.getByText("country4の言語：country4")).toBeVisible();
      expect(screen.getByText("country5の言語：country5")).toBeVisible();
      expect(screen.getByText("Primaryキーカラム：primaryKey")).toBeVisible();
      expect(screen.getByText("Exclusiveキーカラム：exclusiveKey")).toBeVisible();
      expect(screen.getByText("会社コードカラム：companyCodeKey")).toBeVisible();
      expect(screen.getByText("登録者IDカラム：insertedUserIDKey")).toBeVisible();
      expect(screen.getByText("登録日付カラム：insertedDateKey")).toBeVisible();
      expect(screen.getByText("更新者IDカラム：updatedUserIDKey")).toBeVisible();
      expect(screen.getByText("更新日付カラム：updatedDateKey")).toBeVisible();
      expect(screen.getByText("ダウンロードされたファイルのエンコーディング：UTF-8")).toBeVisible();
      expect(screen.getByText("SQL処理にエンティティ定義情報を使用する：true")).toBeVisible();
      expect(screen.getByText("項目のエイリアスを仮想項目名に上書きする：false")).toBeVisible();
      expect(screen.getByText("引数の値をバインドする際に配列を使用するかどうか：false")).toBeVisible();
      expect(screen.getByText("Please fill out this field.")).toBeVisible();
    });
  }
}

export const SearchItem : Story = {
  args: {},
  name: '検索されたアイテムが存在する場合、アイテムを表示される、存在しない場合は表示されないこと。',
  play: async ({canvasElement}) => {
    let alertMessage = null;
    const originalAlert = window.alert;
    window.alert = (msg) => {
      alertMessage = msg;
    };
    const crudTypeSelect = (screen.getByTestId("crud-type-select") as HTMLDivElement).children[0];
    
    await userEvent.click(screen.getByText("新規作成"));
    await userEvent.type(screen.getByPlaceholderText("ID"), "testID");
    await userEvent.click(screen.getByText("カテゴリID"));
    await await userEvent.click(screen.getByText("保存"));
    await userEvent.click(screen.getByText("新規作成"));
    await userEvent.type(screen.getByPlaceholderText("ID"), "testID2");
    await userEvent.click(screen.getByText("カテゴリID"));
    await userEvent.click(screen.getByLabelText("DELETE"));
    await await userEvent.click(screen.getByText("保存"));
    await waitFor(async () => {
      await expect(screen.getByText("仮想表転換の条件一覧")).toBeVisible();
    });
    const searchTextbox = screen.getByLabelText("IDで検索する");
    await userEvent.type(searchTextbox, "testID2");

    await waitFor(() => {
      expect(screen.getByText("testID2")).toBeVisible();
      expect(screen.queryByText("testID")).not.toBeInTheDocument();
    });

    await userEvent.click(crudTypeSelect);
    await userEvent.click(screen.getByText("UPDATE"));
    waitFor(() => {
      expect(screen.getByTestId("empty-input-image")).toBeVisible();
    });
    await userEvent.click(crudTypeSelect);
    await userEvent.click(screen.getByText("CRUDタイプを選んでください"));
    waitFor(() => {
      expect(screen.queryByTestId("empty-input-image")).toBe(null);
      expect(screen.getByText("testID2")).toBeVisible();
    });
    await userEvent.clear(searchTextbox);
    waitFor(() => {
      expect(screen.queryByTestId("empty-input-image")).toBe(null);
      expect(screen.getByText("testID2")).toBeVisible();
      expect(screen.getByText("testID", {exact: true})).toBeVisible();
    });
    window.alert = originalAlert;
  }
}