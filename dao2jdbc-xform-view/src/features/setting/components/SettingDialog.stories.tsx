import type { Meta, StoryObj } from '@storybook/react';

import { useSettingStore } from '../../../store/setting-store';
import { expect, screen, userEvent, waitFor, within } from '@storybook/test';
import { SettingDialog } from './SettingDialog';
import { DbType } from '../../../types/enum';

const meta = {
  component: SettingDialog,
    async beforeEach() {
      useSettingStore.getState().resetSetting();
    }
  
} satisfies Meta<typeof SettingDialog>;

export default meta;

type Story = StoryObj<typeof meta>;

const args = {
  open: true,
  onSubmit: () => {
  },
  initialSetting: {
    id: "",
    savedName: "",
    db: {
      url: "",
      dbType: DbType.ORACLE,
      schema: "",
      username: "",
      password: ""
    },
    locale: {
    current: "ja",
    mapping: {
      country1: "",
      country2: "",
      country3: "",
      country4: "",
      country5: ""
    }
    },
    reservedWords: {
      primaryKey: "",
      exclusiveKey: "",
      companyCodeKey: "",
      insertedUserIDKey: "",
      insertedDateKey: "",
      updatedUserIDKey: "",
      updatedDateKey: ""
    },
    condition: {
      encoding: "UTF-8"
    },
    transform: {
      encoding: "UTF-8",
      forceAliasColumn: false,
      entityQuery: true,
      useForeignKey: false,
      useExpMap: false
    },
    savedTime: ""
  },
  onClose: () => {},
}

export const Default: Story = {
  args: args,
  name: '初期状態の場合、全てのフィールドが空になりこと。',
  play: async ({canvasElement}) => {
    const canvas = within(canvasElement);
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
    
    await expect(localeCurrent.value).toBe("ja");
    await expect(country1.value).toBe("");
    await expect(country2.value).toBe("");
    await expect(country3.value).toBe("");
    await expect(country4.value).toBe("");
    await expect(country5.value).toBe("");
    await expect(dbUrl.value).toBe("");
    await expect(dbType.textContent).toBe("Oracle");
    await expect(dbSchema.value).toBe("");
    await expect(dbUsername.value).toBe("");
    await expect(dbPassword.value).toBe("");
    await expect(primaryKey.value).toBe("");
    await expect(exclusiveKey.value).toBe("");
    await expect(companyCode.value).toBe("");
    await expect(registeredPerson.value).toBe("");
    await expect(registeredDate.value).toBe("");
    await expect(updatedPerson.value).toBe("");
    await expect(updatedDate.value).toBe("");
    await expect(saveNameField.value).toBe("");
    await expect(transformEncoding.value).toBe("UTF-8");
    await expect(forceAliasColumn.checked).toBe(false);
    await expect(entityQuery.checked).toBe(true);
    await expect(useForeignKey.checked).toBe(false);
    await expect(useExpMap.checked).toBe(false);
  }
};

export const AllField: Story = {
  args: args,
  name: '必須のフィールドが入力され、任意フィールドが空の場合、保存ができ、全てのフィールドが元の状態に戻ること。',
  play: async ({canvasElement}) => {
    
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

    
    await userEvent.click(screen.getByRole("button", { name: "保存" }));   
    expect(saveNameField.checkValidity()).toBe(false);
    expect(saveNameField.validationMessage).toBe('このフィールドを入力してください。'); 
    await userEvent.type(saveNameField, "Test Setting");
    
    await userEvent.click(screen.getByRole("button", { name: "保存" }));   
    expect(dbUrl.checkValidity()).toBe(false);
    expect(dbUrl.validationMessage).toBe('このフィールドを入力してください。'); 
    await userEvent.type(dbUrl, "jdbc:oracle:thin:@localhost:1521:xe");

    await userEvent.click(screen.getByRole("button", { name: "保存" }));   
    expect(dbSchema.checkValidity()).toBe(false);
    expect(dbSchema.validationMessage).toBe('このフィールドを入力してください。'); 
    await userEvent.type(dbSchema, "test_schema");

    await userEvent.click(screen.getByRole("button", { name: "保存" }));   
    expect(dbUsername.checkValidity()).toBe(false);
    expect(dbUsername.validationMessage).toBe('このフィールドを入力してください。'); 
    await userEvent.type(dbUsername, "test_user");
    
    await userEvent.click(screen.getByRole("button", { name: "保存" }));   
    expect(dbPassword.checkValidity()).toBe(false);
    expect(dbPassword.validationMessage).toBe('このフィールドを入力してください。'); 
    await userEvent.type(dbPassword, "test_password");
    
    await userEvent.click(screen.getByRole("button", { name: "保存" }));   
    expect(country1.checkValidity()).toBe(false);
    expect(country1.validationMessage).toBe('このフィールドを入力してください。'); 
    await userEvent.type(country1, "ja");
    
    await userEvent.click(screen.getByRole("button", { name: "保存" }));   
    expect(primaryKey.checkValidity()).toBe(false);
    expect(primaryKey.validationMessage).toBe('このフィールドを入力してください。'); 
    await userEvent.type(primaryKey, "id");

    await userEvent.click(screen.getByRole("button", { name: "保存" }));   
    expect(exclusiveKey.checkValidity()).toBe(false);
    expect(exclusiveKey.validationMessage).toBe('このフィールドを入力してください。'); 
    await userEvent.type(exclusiveKey, "exclusive_id");

    await userEvent.click(screen.getByRole("button", { name: "保存" }));   
    expect(companyCode.checkValidity()).toBe(false);
    expect(companyCode.validationMessage).toBe('このフィールドを入力してください。'); 
    await userEvent.type(companyCode, "company_code");
    
    await userEvent.click(screen.getByRole("button", { name: "保存" }));   
    expect(registeredPerson.checkValidity()).toBe(false);
    expect(registeredPerson.validationMessage).toBe('このフィールドを入力してください。'); 
    await userEvent.type(registeredPerson, "inserted_user_id");
    
    await userEvent.click(screen.getByRole("button", { name: "保存" }));   
    expect(registeredDate.checkValidity()).toBe(false);
    expect(registeredDate.validationMessage).toBe('このフィールドを入力してください。'); 
    await userEvent.type(registeredDate, "inserted_date");

    await userEvent.click(screen.getByRole("button", { name: "保存" }));   
    expect(updatedPerson.checkValidity()).toBe(false);
    expect(updatedPerson.validationMessage).toBe('このフィールドを入力してください。'); 
    await userEvent.type(updatedPerson, "updated_user_id");

    await userEvent.click(screen.getByRole("button", { name: "保存" }));   
    expect(updatedDate.checkValidity()).toBe(false);
    expect(updatedDate.validationMessage).toBe('このフィールドを入力してください。'); 
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
    
    await expect(transformEncoding.value).toBe("UTF-8");
    await expect(forceAliasColumn.checked).toBe(false);
    await expect(entityQuery.checked).toBe(true);
    await expect(useForeignKey.checked).toBe(false);
    await expect(useExpMap.checked).toBe(false);
    await userEvent.click(screen.getByRole("button", { name: "保存" })); 

  }
};

