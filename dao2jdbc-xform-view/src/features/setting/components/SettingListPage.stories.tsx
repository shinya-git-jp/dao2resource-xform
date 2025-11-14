import type { Meta, StoryObj } from '@storybook/react';

import SettingListPage from './SettingListPage';
import { useSettingStore } from '../../../store/setting-store';
import { expect, screen, userEvent, waitFor, within } from '@storybook/test';

const meta = {
  component: SettingListPage,
  async beforeEach() {
    useSettingStore.getState().resetSettings();
  }
} satisfies Meta<typeof SettingListPage>;

export default meta;

type Story = StoryObj<typeof meta>;


export const Default: Story = {
  args: {},
  name: '初期状態の場合、全てのフィールドが空になりこと。',
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
    
    await expect(canvas.getByTestId("empty-setting-image")).toBeVisible();
  }
};

export const NewButtonOnClicked: Story = {
  args: {},
  name: '「新規作成」ボタンを押下すると、設定作成ダイヤログが表示されること。',
  play: async ({canvasElement}) => {
    const newButton = screen.getByRole("button", { name: "新規作成" });
    await newButton.click();
    const dialog = screen.getByRole("dialog");
    await waitFor(async () => {
      await expect(dialog).toBeVisible();
    });
  }
};

export const SettingDialogOnClose: Story = {
  args: {},
  name: '設定作成ダイヤログにクロスボタンを押下すると、ダイヤログが閉じれること。',
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
    const newButton = canvas.getByRole("button", { name: "新規作成" });
    await newButton.click();
    const dialog = screen.getByRole("dialog");
    const closeButton = within(dialog).getByRole("button", { name: "close" });
    await userEvent.click(closeButton);
    await waitFor(() => {
      expect(dialog).not.toBeVisible();
    });
  }
};

export const SettingDialogNewItem: Story = {
  args: {},
  name: '設定作成ダイヤログに正しい情報が入力されると、新しいアイテムが追加されること。',
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
    const newButton = canvas.getByRole("button", { name: "新規作成" });
    await userEvent.click(newButton);
    const dialog = screen.getByRole("dialog");
    await waitFor(async () => {
      await expect(dialog).toBeVisible();
    });
    let alertMessage = null;
    const originalAlert = window.alert;
    window.alert = (msg) => {
      alertMessage = msg;
    };
    
    const saveNameField = screen.getByPlaceholderText("設定名") as HTMLInputElement;
    await screen.getByText("アウトプット情報").click();
    const transformEncoding = screen.getByPlaceholderText("ダウンロードされたファイルのエンコーディング") as HTMLInputElement;
    const forceAliasColumn = screen.getByRole("checkbox", { name: "項目のエイリアスを仮想項目名に上書きする" }) as HTMLInputElement;
    const entityQuery =  screen.getByRole("checkbox", { name: "SQL処理にエンティティ定義情報を使用する" }) as HTMLInputElement;
    const useForeignKey = screen.getByRole("checkbox", { name: "エンティティ定義の外部キーを使用する" }) as HTMLInputElement;
    const useExpMap = screen.getByRole("checkbox", { name: "引数の値をバインドする際にMapを使用するかどうか" }) as HTMLInputElement;
    await screen.getByText("データベース情報").click();
    const dbUrl = screen.getByPlaceholderText("URL") as HTMLInputElement;
    const dbType = (screen.getByTestId("selected-setting") as HTMLDivElement).children[0];
    const dbSchema = screen.getByPlaceholderText("Scheme") as HTMLInputElement;
    const dbUsername = screen.getByPlaceholderText("Username") as HTMLInputElement;
    const dbPassword = screen.getByPlaceholderText("Password") as HTMLInputElement;
    await screen.getByText("言語情報").click();
    const localeCurrent = screen.getByPlaceholderText("使用する言語") as HTMLInputElement;
    const country1 = screen.getByPlaceholderText("country1の言語") as HTMLInputElement;
    const country2 = screen.getByPlaceholderText("country2の言語") as HTMLInputElement;
    const country3 = screen.getByPlaceholderText("country3の言語") as HTMLInputElement;
    const country4 = screen.getByPlaceholderText("country4の言語") as HTMLInputElement;
    const country5 = screen.getByPlaceholderText("country5の言語") as HTMLInputElement;
    await screen.getByText("予約語情報").click();
    const primaryKey = screen.getByPlaceholderText("Primaryキーカラム") as HTMLInputElement;
    const exclusiveKey = screen.getByPlaceholderText("Exclusiveキーカラム") as HTMLInputElement;
    const companyCode = screen.getByPlaceholderText("会社コードカラム") as HTMLInputElement;
    const registeredPerson = screen.getByPlaceholderText("登録者IDカラム") as HTMLInputElement;
    const registeredDate = screen.getByPlaceholderText("登録日付カラム") as HTMLInputElement;
    const updatedPerson = screen.getByPlaceholderText("更新者IDカラム") as HTMLInputElement;
    const updatedDate = screen.getByPlaceholderText("更新日付カラム") as HTMLInputElement;
    await userEvent.type(saveNameField, "Test Setting");
    await userEvent.type(dbUrl, "jdbc:oracle:thin:@localhost:1521:xe");
    await userEvent.type(dbSchema, "test_schema");
    await userEvent.type(dbUsername, "test_user");
    await userEvent.type(dbPassword, "test_password");
    await userEvent.type(country1, "ja");
    await userEvent.type(primaryKey, "id");
    await userEvent.type(exclusiveKey, "exclusive_id");
    await userEvent.type(companyCode, "company_code");
    await userEvent.type(registeredPerson, "inserted_user_id");
    await userEvent.type(registeredDate, "inserted_date");
    await userEvent.type(updatedPerson, "updated_user_id");
    await userEvent.type(updatedDate, "updated_date");

    
    await userEvent.click(screen.getByRole("button", { name: "保存" }));  
    window.alert = originalAlert;
    const closeButton = within(dialog).getByRole("button", { name: "close" });
    await userEvent.click(closeButton);
    await waitFor(() => {
      expect(dialog).not.toBeVisible();
    });
    await expect(screen.getByText("Test Setting")).toBeVisible();
  }
};


export const SettingDialogNewFalseItem: Story = {
  args: {},
  name: '設定作成ダイヤログに不正解情報が入力されると、新しいアイテムが追加されないこと。',
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
    const newButton = canvas.getByRole("button", { name: "新規作成" });
    await userEvent.click(newButton);
    const dialog = screen.getByRole("dialog");
    await waitFor(async () => {
      await expect(dialog).toBeVisible();
    });
    const saveNameField = screen.getByPlaceholderText("設定名") as HTMLInputElement;

    expect(saveNameField.checkValidity()).toBe(false);
    expect(saveNameField.validationMessage).toBe('このフィールドを入力してください。'); 
    
    await expect(saveNameField.value).toBe("");
    const closeButton = within(dialog).getByRole("button", { name: "close" });
    await userEvent.click(closeButton);
    await waitFor(() => {
      expect(dialog).not.toBeVisible();
    });
    await expect(screen.queryByText("Test Setting")).toBe(null);
  }
};

export const CheckedSettingItem: Story = {
  args: {},
  name: '設定アイテムにチェックボックスをチェックすると、削除ボタンとエクスポートボタンがアクティブになること。',
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
    const newButton = canvas.getByRole("button", { name: "新規作成" });
    await userEvent.click(newButton);
    const dialog = screen.getByRole("dialog");
    await waitFor(async () => {
      await expect(dialog).toBeVisible();
    });
    let alertMessage = null;
    const originalAlert = window.alert;
    window.alert = (msg) => {
      alertMessage = msg;
    };
   
    const saveNameField = screen.getByPlaceholderText("設定名") as HTMLInputElement;
    await screen.getByText("アウトプット情報").click();
    await screen.getByText("データベース情報").click();
    const dbUrl = screen.getByPlaceholderText("URL") as HTMLInputElement;
    const dbType = (screen.getByTestId("selected-setting") as HTMLDivElement).children[0];
    const dbSchema = screen.getByPlaceholderText("Scheme") as HTMLInputElement;
    const dbUsername = screen.getByPlaceholderText("Username") as HTMLInputElement;
    const dbPassword = screen.getByPlaceholderText("Password") as HTMLInputElement;
    await screen.getByText("言語情報").click();
    const localeCurrent = screen.getByPlaceholderText("使用する言語") as HTMLInputElement;
    const country1 = screen.getByPlaceholderText("country1の言語") as HTMLInputElement;
    const country2 = screen.getByPlaceholderText("country2の言語") as HTMLInputElement;
    const country3 = screen.getByPlaceholderText("country3の言語") as HTMLInputElement;
    const country4 = screen.getByPlaceholderText("country4の言語") as HTMLInputElement;
    const country5 = screen.getByPlaceholderText("country5の言語") as HTMLInputElement;
    await screen.getByText("予約語情報").click();
    const primaryKey = screen.getByPlaceholderText("Primaryキーカラム") as HTMLInputElement;
    const exclusiveKey = screen.getByPlaceholderText("Exclusiveキーカラム") as HTMLInputElement;
    const companyCode = screen.getByPlaceholderText("会社コードカラム") as HTMLInputElement;
    const registeredPerson = screen.getByPlaceholderText("登録者IDカラム") as HTMLInputElement;
    const registeredDate = screen.getByPlaceholderText("登録日付カラム") as HTMLInputElement;
    const updatedPerson = screen.getByPlaceholderText("更新者IDカラム") as HTMLInputElement;
    const updatedDate = screen.getByPlaceholderText("更新日付カラム") as HTMLInputElement;
    await userEvent.type(saveNameField, "Test Setting");
    await userEvent.type(dbUrl, "jdbc:oracle:thin:@localhost:1521:xe");
    await userEvent.type(dbSchema, "test_schema");
    await userEvent.type(dbUsername, "test_user");
    await userEvent.type(dbPassword, "test_password");
    await userEvent.type(country1, "ja");
    await userEvent.type(primaryKey, "id");
    await userEvent.type(exclusiveKey, "exclusive_id");
    await userEvent.type(companyCode, "company_code");
    await userEvent.type(registeredPerson, "inserted_user_id");
    await userEvent.type(registeredDate, "inserted_date");
    await userEvent.type(updatedPerson, "updated_user_id");
    await userEvent.type(updatedDate, "updated_date");

    
    await userEvent.click(screen.getByRole("button", { name: "保存" }));   

    waitFor(() => {
      expect(alertMessage).toBe("保存しました。");
    });
    
    window.alert = originalAlert;
    const closeButton = within(dialog).getByRole("button", { name: "close" });
    await userEvent.click(closeButton);
    await waitFor(() => {
      expect(dialog).not.toBeVisible();
    });
    await expect(screen.getByText("Test Setting")).toBeVisible();
    const checkbox = screen.getByTestId("setting-checkbox-0");
    await userEvent.click(checkbox.children[0]);
    const deleteButton = screen.getByRole("button", { name: "削除" });
    const exportButton = screen.getByRole("button", { name: "エクスポート" });
    await expect(deleteButton).toBeEnabled();
    await expect(exportButton).toBeEnabled();
  }
};

export const UncheckSetting: Story = {
  args: {},
  name: '設定アイテムにチェックボックスをチェックすると、削除ボタンとエクスポートボタンがアクティブになること。',
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
    const newButton = canvas.getByRole("button", { name: "新規作成" });
    await userEvent.click(newButton);
    const dialog = screen.getByRole("dialog");
    await waitFor(async () => {
      await expect(dialog).toBeVisible();
    });
    let alertMessage = null;
    const originalAlert = window.alert;
    window.alert = (msg) => {
      alertMessage = msg;
    };
   
    const saveNameField = screen.getByPlaceholderText("設定名") as HTMLInputElement;
    await screen.getByText("アウトプット情報").click();
    await screen.getByText("データベース情報").click();
    const dbUrl = screen.getByPlaceholderText("URL") as HTMLInputElement;
    const dbType = (screen.getByTestId("selected-setting") as HTMLDivElement).children[0];
    const dbSchema = screen.getByPlaceholderText("Scheme") as HTMLInputElement;
    const dbUsername = screen.getByPlaceholderText("Username") as HTMLInputElement;
    const dbPassword = screen.getByPlaceholderText("Password") as HTMLInputElement;
    await screen.getByText("言語情報").click();
    const localeCurrent = screen.getByPlaceholderText("使用する言語") as HTMLInputElement;
    const country1 = screen.getByPlaceholderText("country1の言語") as HTMLInputElement;
    const country2 = screen.getByPlaceholderText("country2の言語") as HTMLInputElement;
    const country3 = screen.getByPlaceholderText("country3の言語") as HTMLInputElement;
    const country4 = screen.getByPlaceholderText("country4の言語") as HTMLInputElement;
    const country5 = screen.getByPlaceholderText("country5の言語") as HTMLInputElement;
    await screen.getByText("予約語情報").click();
    const primaryKey = screen.getByPlaceholderText("Primaryキーカラム") as HTMLInputElement;
    const exclusiveKey = screen.getByPlaceholderText("Exclusiveキーカラム") as HTMLInputElement;
    const companyCode = screen.getByPlaceholderText("会社コードカラム") as HTMLInputElement;
    const registeredPerson = screen.getByPlaceholderText("登録者IDカラム") as HTMLInputElement;
    const registeredDate = screen.getByPlaceholderText("登録日付カラム") as HTMLInputElement;
    const updatedPerson = screen.getByPlaceholderText("更新者IDカラム") as HTMLInputElement;
    const updatedDate = screen.getByPlaceholderText("更新日付カラム") as HTMLInputElement;
    await userEvent.type(saveNameField, "Test Setting");
    await userEvent.type(dbUrl, "jdbc:oracle:thin:@localhost:1521:xe");
    await userEvent.type(dbSchema, "test_schema");
    await userEvent.type(dbUsername, "test_user");
    await userEvent.type(dbPassword, "test_password");
    await userEvent.type(country1, "ja");
    await userEvent.type(primaryKey, "id");
    await userEvent.type(exclusiveKey, "exclusive_id");
    await userEvent.type(companyCode, "company_code");
    await userEvent.type(registeredPerson, "inserted_user_id");
    await userEvent.type(registeredDate, "inserted_date");
    await userEvent.type(updatedPerson, "updated_user_id");
    await userEvent.type(updatedDate, "updated_date");

    
    await userEvent.click(screen.getByRole("button", { name: "保存" }));   

    waitFor(() => {
      expect(alertMessage).toBe("保存しました。");
    });
    
    window.alert = originalAlert;
    const closeButton = within(dialog).getByRole("button", { name: "close" });
    await userEvent.click(closeButton);
    await waitFor(() => {
      expect(dialog).not.toBeVisible();
    });
    await expect(screen.getByText("Test Setting")).toBeVisible();
    const checkbox = screen.getByTestId("setting-checkbox-0");
    await userEvent.click(checkbox.children[0]);
    const deleteButton = screen.getByRole("button", { name: "削除" });
    const exportButton = screen.getByRole("button", { name: "エクスポート" });
    await expect(deleteButton).toBeEnabled();
    await expect(exportButton).toBeEnabled();
    await userEvent.click(checkbox.children[0]);
    await expect(deleteButton).toBeDisabled();
    await expect(exportButton).toBeDisabled();
  }
};

export const ShowSettingDetails: Story = {
  args: {},
  name: '設定アイテムを押下すると、ダイヤログが表示され、正しい情報が表示されること。',
  play: async ({canvasElement}) => {
    
    const canvas = within(canvasElement);
    const newButton = canvas.getByRole("button", { name: "新規作成" });
    await userEvent.click(newButton);
    const dialog = screen.getByRole("dialog");
    await waitFor(async () => {
      await expect(dialog).toBeVisible();
    });
    let alertMessage = null;
    const originalAlert = window.alert;
    window.alert = (msg) => {
      alertMessage = msg;
    };
   
    const saveNameField = screen.getByPlaceholderText("設定名") as HTMLInputElement;
    await screen.getByText("アウトプット情報").click();
    const transformEncoding = screen.getByPlaceholderText("ダウンロードされたファイルのエンコーディング") as HTMLInputElement;
    const forceAliasColumn = screen.getByRole("checkbox", { name: "項目のエイリアスを仮想項目名に上書きする" }) as HTMLInputElement;
    const entityQuery =  screen.getByRole("checkbox", { name: "SQL処理にエンティティ定義情報を使用する" }) as HTMLInputElement;
    const useForeignKey = screen.getByRole("checkbox", { name: "エンティティ定義の外部キーを使用する" }) as HTMLInputElement;
    const useExpMap = screen.getByRole("checkbox", { name: "引数の値をバインドする際にMapを使用するかどうか" }) as HTMLInputElement;
    await screen.getByText("データベース情報").click();
    const dbUrl = screen.getByPlaceholderText("URL") as HTMLInputElement;
    const dbType = (screen.getByTestId("selected-setting") as HTMLDivElement).children[0];
    const dbSchema = screen.getByPlaceholderText("Scheme") as HTMLInputElement;
    const dbUsername = screen.getByPlaceholderText("Username") as HTMLInputElement;
    const dbPassword = screen.getByPlaceholderText("Password") as HTMLInputElement;
    await screen.getByText("言語情報").click();
    const localeCurrent = screen.getByPlaceholderText("使用する言語") as HTMLInputElement;
    const country1 = screen.getByPlaceholderText("country1の言語") as HTMLInputElement;
    const country2 = screen.getByPlaceholderText("country2の言語") as HTMLInputElement;
    const country3 = screen.getByPlaceholderText("country3の言語") as HTMLInputElement;
    const country4 = screen.getByPlaceholderText("country4の言語") as HTMLInputElement;
    const country5 = screen.getByPlaceholderText("country5の言語") as HTMLInputElement;
    await screen.getByText("予約語情報").click();
    const primaryKey = screen.getByPlaceholderText("Primaryキーカラム") as HTMLInputElement;
    const exclusiveKey = screen.getByPlaceholderText("Exclusiveキーカラム") as HTMLInputElement;
    const companyCode = screen.getByPlaceholderText("会社コードカラム") as HTMLInputElement;
    const registeredPerson = screen.getByPlaceholderText("登録者IDカラム") as HTMLInputElement;
    const registeredDate = screen.getByPlaceholderText("登録日付カラム") as HTMLInputElement;
    const updatedPerson = screen.getByPlaceholderText("更新者IDカラム") as HTMLInputElement;
    const updatedDate = screen.getByPlaceholderText("更新日付カラム") as HTMLInputElement;
    await userEvent.type(saveNameField, "Test Setting");
    await userEvent.type(dbUrl, "jdbc:oracle:thin:@localhost:1521:xe");
    await userEvent.type(dbSchema, "test_schema");
    await userEvent.type(dbUsername, "test_user");
    await userEvent.type(dbPassword, "test_password");
    await userEvent.type(country1, "ja");
    await userEvent.type(primaryKey, "id");
    await userEvent.type(exclusiveKey, "exclusive_id");
    await userEvent.type(companyCode, "company_code");
    await userEvent.type(registeredPerson, "inserted_user_id");
    await userEvent.type(registeredDate, "inserted_date");
    await userEvent.type(updatedPerson, "updated_user_id");
    await userEvent.type(updatedDate, "updated_date");

    
    
    await expect(saveNameField.value).toBe("Test Setting");
    await expect(dbUrl.value).toBe("jdbc:oracle:thin:@localhost:1521:xe");
    await expect(dbType.textContent).toBe("Oracle");
    await expect(dbSchema.value).toBe("test_schema");
    await expect(dbUsername.value).toBe("test_user");
    await expect(dbPassword.value).toBe("test_password");
    await expect(localeCurrent.value).toBe("ja");
    await expect(country1.value).toBe("ja");
    await expect(country2.value).toBe("");
    await expect(country3.value).toBe("");
    await expect(country4.value).toBe("");
    await expect(country5.value).toBe("");
    await expect(primaryKey.value).toBe("id");
    await expect(exclusiveKey.value).toBe("exclusive_id");
    await expect(companyCode.value).toBe("company_code");
    await expect(registeredPerson.value).toBe("inserted_user_id");
    await expect(registeredDate.value).toBe("inserted_date");
    await expect(updatedPerson.value).toBe("updated_user_id");
    await expect(updatedDate.value).toBe("updated_date");
    await userEvent.click(screen.getByRole("button", { name: "保存" }));   

    waitFor(() => {
      expect(alertMessage).toBe("保存しました。");
    });
    
    window.alert = originalAlert;
    const closeButton = within(dialog).getByRole("button", { name: "close" });
    await userEvent.click(closeButton);
    await userEvent.click(screen.getByTestId("setting-item-0"));
    await waitFor(() => {
      expect(screen.getByRole("dialog")).toBeVisible();
    });
    
    await expect(saveNameField.value).toBe("Test Setting");
    await expect(dbUrl.value).toBe("jdbc:oracle:thin:@localhost:1521:xe");
    await expect(dbType.textContent).toBe("Oracle");
    await expect(dbSchema.value).toBe("test_schema");
    await expect(dbUsername.value).toBe("test_user");
    await expect(dbPassword.value).toBe("test_password");
    await expect(localeCurrent.value).toBe("ja");
    await expect(country1.value).toBe("ja");
    await expect(country2.value).toBe("");
    await expect(country3.value).toBe("");
    await expect(country4.value).toBe("");
    await expect(country5.value).toBe("");
    await expect(primaryKey.value).toBe("id");
    await expect(exclusiveKey.value).toBe("exclusive_id");
    await expect(companyCode.value).toBe("company_code");
    await expect(registeredPerson.value).toBe("inserted_user_id");
    await expect(registeredDate.value).toBe("inserted_date");
    await expect(updatedPerson.value).toBe("updated_user_id");
    await expect(updatedDate.value).toBe("updated_date");
  }
};


export const UpdateSetting: Story = {
  args: {},
  name: '設定アイテムを押下すると、ダイヤログが表示され、情報を変更すると、情報が更新されること。',
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
    const newButton = canvas.getByRole("button", { name: "新規作成" });
    await userEvent.click(newButton);
    const dialog = screen.getByRole("dialog");
    await waitFor(async () => {
      await expect(dialog).toBeVisible();
    });
    let alertMessage = null;
    const originalAlert = window.alert;
    window.alert = (msg) => {
      alertMessage = msg;
    };
    const saveNameField = screen.getByPlaceholderText("設定名") as HTMLInputElement;
    await screen.getByText("アウトプット情報").click();
    const transformEncoding = screen.getByPlaceholderText("ダウンロードされたファイルのエンコーディング") as HTMLInputElement;
    const forceAliasColumn = screen.getByRole("checkbox", { name: "項目のエイリアスを仮想項目名に上書きする" }) as HTMLInputElement;
    const entityQuery =  screen.getByRole("checkbox", { name: "SQL処理にエンティティ定義情報を使用する" }) as HTMLInputElement;
    const useForeignKey = screen.getByRole("checkbox", { name: "エンティティ定義の外部キーを使用する" }) as HTMLInputElement;
    const useExpMap = screen.getByRole("checkbox", { name: "引数の値をバインドする際にMapを使用するかどうか" }) as HTMLInputElement;
    await screen.getByText("データベース情報").click();
    const dbUrl = screen.getByPlaceholderText("URL") as HTMLInputElement;
    const dbType = (screen.getByTestId("selected-setting") as HTMLDivElement).children[0];
    const dbSchema = screen.getByPlaceholderText("Scheme") as HTMLInputElement;
    const dbUsername = screen.getByPlaceholderText("Username") as HTMLInputElement;
    const dbPassword = screen.getByPlaceholderText("Password") as HTMLInputElement;
    await screen.getByText("言語情報").click();
    const localeCurrent = screen.getByPlaceholderText("使用する言語") as HTMLInputElement;
    const country1 = screen.getByPlaceholderText("country1の言語") as HTMLInputElement;
    const country2 = screen.getByPlaceholderText("country2の言語") as HTMLInputElement;
    const country3 = screen.getByPlaceholderText("country3の言語") as HTMLInputElement;
    const country4 = screen.getByPlaceholderText("country4の言語") as HTMLInputElement;
    const country5 = screen.getByPlaceholderText("country5の言語") as HTMLInputElement;
    await screen.getByText("予約語情報").click();
    const primaryKey = screen.getByPlaceholderText("Primaryキーカラム") as HTMLInputElement;
    const exclusiveKey = screen.getByPlaceholderText("Exclusiveキーカラム") as HTMLInputElement;
    const companyCode = screen.getByPlaceholderText("会社コードカラム") as HTMLInputElement;
    const registeredPerson = screen.getByPlaceholderText("登録者IDカラム") as HTMLInputElement;
    const registeredDate = screen.getByPlaceholderText("登録日付カラム") as HTMLInputElement;
    const updatedPerson = screen.getByPlaceholderText("更新者IDカラム") as HTMLInputElement;
    const updatedDate = screen.getByPlaceholderText("更新日付カラム") as HTMLInputElement;
    await userEvent.type(saveNameField, "Test Setting");
    await userEvent.type(dbUrl, "jdbc:oracle:thin:@localhost:1521:xe");
    await userEvent.type(dbSchema, "test_schema");
    await userEvent.type(dbUsername, "test_user");
    await userEvent.type(dbPassword, "test_password");
    await userEvent.type(country1, "ja");
    await userEvent.type(primaryKey, "id");
    await userEvent.type(exclusiveKey, "exclusive_id");
    await userEvent.type(companyCode, "company_code");
    await userEvent.type(registeredPerson, "inserted_user_id");
    await userEvent.type(registeredDate, "inserted_date");
    await userEvent.type(updatedPerson, "updated_user_id");
    await userEvent.type(updatedDate, "updated_date");
    await userEvent.click(screen.getByRole("button", { name: "保存" }));   

    waitFor(() => {
      expect(alertMessage).toBe("保存しました。");
    });

    const closeButton = within(dialog).getByRole("button", { name: "close" });
    await userEvent.click(closeButton);
    await userEvent.click(screen.getByTestId("setting-item-0"));
    await waitFor(() => {
      expect(screen.getByRole("dialog")).toBeVisible();
    });

    await userEvent.clear(saveNameField);
    await userEvent.type(saveNameField, "Updated Setting");
    await userEvent.click(screen.getByRole("button", { name: "保存" }));
    await userEvent.click(closeButton);
    await waitFor(() => {
      expect(screen.getByText("Updated Setting")).toBeVisible();
    });
    window.alert = originalAlert;
  }
};

export const DeleteSettingItem: Story = {
  args: {},
  name: '削除ボタンを押下すると、チェックされるアイテムが削除されること。',
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
    const newButton = canvas.getByRole("button", { name: "新規作成" });
    await userEvent.click(newButton);
    const dialog = screen.getByRole("dialog");
    await waitFor(async () => {
      await expect(dialog).toBeVisible();
    });
    let alertMessage = null;
    const originalAlert = window.alert;
    const originalConfirm = window.confirm;
    window.alert = (msg) => {
      alertMessage = msg;
    };
    window.confirm = () => true; 
    const saveNameField = screen.getByPlaceholderText("設定名") as HTMLInputElement;
    await screen.getByText("アウトプット情報").click();
    const transformEncoding = screen.getByPlaceholderText("ダウンロードされたファイルのエンコーディング") as HTMLInputElement;
    const forceAliasColumn = screen.getByRole("checkbox", { name: "項目のエイリアスを仮想項目名に上書きする" }) as HTMLInputElement;
    const entityQuery =  screen.getByRole("checkbox", { name: "SQL処理にエンティティ定義情報を使用する" }) as HTMLInputElement;
    const useForeignKey = screen.getByRole("checkbox", { name: "エンティティ定義の外部キーを使用する" }) as HTMLInputElement;
    const useExpMap = screen.getByRole("checkbox", { name: "引数の値をバインドする際にMapを使用するかどうか" }) as HTMLInputElement;
    await screen.getByText("データベース情報").click();
    const dbUrl = screen.getByPlaceholderText("URL") as HTMLInputElement;
    const dbType = (screen.getByTestId("selected-setting") as HTMLDivElement).children[0];
    const dbSchema = screen.getByPlaceholderText("Scheme") as HTMLInputElement;
    const dbUsername = screen.getByPlaceholderText("Username") as HTMLInputElement;
    const dbPassword = screen.getByPlaceholderText("Password") as HTMLInputElement;
    await screen.getByText("言語情報").click();
    const localeCurrent = screen.getByPlaceholderText("使用する言語") as HTMLInputElement;
    const country1 = screen.getByPlaceholderText("country1の言語") as HTMLInputElement;
    const country2 = screen.getByPlaceholderText("country2の言語") as HTMLInputElement;
    const country3 = screen.getByPlaceholderText("country3の言語") as HTMLInputElement;
    const country4 = screen.getByPlaceholderText("country4の言語") as HTMLInputElement;
    const country5 = screen.getByPlaceholderText("country5の言語") as HTMLInputElement;
    await screen.getByText("予約語情報").click();
    const primaryKey = screen.getByPlaceholderText("Primaryキーカラム") as HTMLInputElement;
    const exclusiveKey = screen.getByPlaceholderText("Exclusiveキーカラム") as HTMLInputElement;
    const companyCode = screen.getByPlaceholderText("会社コードカラム") as HTMLInputElement;
    const registeredPerson = screen.getByPlaceholderText("登録者IDカラム") as HTMLInputElement;
    const registeredDate = screen.getByPlaceholderText("登録日付カラム") as HTMLInputElement;
    const updatedPerson = screen.getByPlaceholderText("更新者IDカラム") as HTMLInputElement;
    const updatedDate = screen.getByPlaceholderText("更新日付カラム") as HTMLInputElement;
    await userEvent.type(saveNameField, "Test Setting");
    await userEvent.type(dbUrl, "jdbc:oracle:thin:@localhost:1521:xe");
    await userEvent.type(dbSchema, "test_schema");
    await userEvent.type(dbUsername, "test_user");
    await userEvent.type(dbPassword, "test_password");
    await userEvent.type(country1, "ja");
    await userEvent.type(primaryKey, "id");
    await userEvent.type(exclusiveKey, "exclusive_id");
    await userEvent.type(companyCode, "company_code");
    await userEvent.type(registeredPerson, "inserted_user_id");
    await userEvent.type(registeredDate, "inserted_date");
    await userEvent.type(updatedPerson, "updated_user_id");
    await userEvent.type(updatedDate, "updated_date");
    await userEvent.click(screen.getByRole("button", { name: "保存" }));   

    waitFor(() => {
      expect(alertMessage).toBe("保存しました。");
    });
    
    const closeButton = within(dialog).getByRole("button", { name: "close" });
    await userEvent.click(closeButton);
    
    const checkbox = screen.getByTestId("setting-checkbox-0");
    await userEvent.click(checkbox.children[0]);
    await waitFor(async () => {
      const deleteButton = screen.getByRole("button", { name: "削除" });

      await userEvent.click(deleteButton);
    });
    await waitFor(() => {
      expect(screen.queryByText("Test Setting")).toBe(null);
      expect(screen.getByRole("button", { name: "削除" })).toBeDisabled();
      expect(screen.getByRole("button", { name: "エクスポート" })).toBeDisabled();
    });
    window.alert = originalAlert;
    window.confirm = originalConfirm;
  }
};