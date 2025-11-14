import { test, expect, Page } from '@playwright/test';
import { DEFAULT_URL } from './constants';
import { InputListPage } from './entity-input-list';
import { SettingListPage } from './setting-list';
import * as fs from 'fs';

test.describe.configure({mode: "serial"});
test.slow();
test.beforeEach(async ({page})=> {
    await page.goto(DEFAULT_URL);
})
const addFalseSetting  = async (page: Page) => {
    
    const settingListPage = new SettingListPage(page);
    await settingListPage.openSettingPage();
    await settingListPage.clickNewItemButton();
    await settingListPage.settingDialog.selectDatabaseSettingsSection();
    await settingListPage.settingDialog.setUrlField("jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9");
    await settingListPage.settingDialog.setDbTypeField("MySQL");
    await settingListPage.settingDialog.setSchemaField("gef_jdbc_tool");
    await settingListPage.settingDialog.setUsernameField("geframe2");
    await settingListPage.settingDialog.setPasswordField("Kccs0000");
    await settingListPage.settingDialog.selectReservedWordsSettingsSection();
    await settingListPage.settingDialog.setPrimaryKeyField("objectID");
    await settingListPage.settingDialog.setExclusiveKeyField("ExclusiveFG");
    await settingListPage.settingDialog.setCompanyCodeField("CompanyCD");
    await settingListPage.settingDialog.setInsertedUserIdField("RegisteredPerson");
    await settingListPage.settingDialog.setInsertedDateField("RegisteredDT");
    await settingListPage.settingDialog.setUpdatedUserIdField("UpdatedPerson");
    await settingListPage.settingDialog.setUpdatedDateField("UpdatedDT");
    await settingListPage.settingDialog.selectLocaleSettingsSection();
    await settingListPage.settingDialog.setLocaleField("en");
    await settingListPage.settingDialog.setCountry1Field("en");
    await settingListPage.settingDialog.setCountry2Field("jp");
    await settingListPage.settingDialog.setCountry3Field("id");
    await settingListPage.settingDialog.setCountry4Field("zh");
    await settingListPage.settingDialog.setCountry5Field("us");
    await settingListPage.settingDialog.selectTransformSettingsSection();
    await settingListPage.settingDialog.checkForceAliasColumn();
    await settingListPage.settingDialog.checkUseExpMap();
    await settingListPage.settingDialog.checkUseForeignKey();
    await settingListPage.settingDialog.clickSaveButton("test");
    await settingListPage.settingDialog.clickCloseButton();
}
const addSetting = async (page: Page) => {
    const settingListPage = new SettingListPage(page);
    await settingListPage.openSettingPage();
    await settingListPage.clickNewItemButton();
    await settingListPage.settingDialog.selectDatabaseSettingsSection();
    await settingListPage.settingDialog.setUrlField("jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9");
    await settingListPage.settingDialog.setDbTypeField("MySQL");
    await settingListPage.settingDialog.setSchemaField("gef_jdbc_tool");
    await settingListPage.settingDialog.setUsernameField("geframe");
    await settingListPage.settingDialog.setPasswordField("Kccs0000");
    await settingListPage.settingDialog.selectReservedWordsSettingsSection();
    await settingListPage.settingDialog.setPrimaryKeyField("objectID");
    await settingListPage.settingDialog.setExclusiveKeyField("ExclusiveFG");
    await settingListPage.settingDialog.setCompanyCodeField("CompanyCD");
    await settingListPage.settingDialog.setInsertedUserIdField("RegisteredPerson");
    await settingListPage.settingDialog.setInsertedDateField("RegisteredDT");
    await settingListPage.settingDialog.setUpdatedUserIdField("UpdatedPerson");
    await settingListPage.settingDialog.setUpdatedDateField("UpdatedDT");
    await settingListPage.settingDialog.selectLocaleSettingsSection();
    await settingListPage.settingDialog.setLocaleField("en");
    await settingListPage.settingDialog.setCountry1Field("en");
    await settingListPage.settingDialog.setCountry2Field("jp");
    await settingListPage.settingDialog.setCountry3Field("id");
    await settingListPage.settingDialog.setCountry4Field("zh");
    await settingListPage.settingDialog.setCountry5Field("us");
    await settingListPage.settingDialog.selectTransformSettingsSection();
    await settingListPage.settingDialog.checkForceAliasColumn();
    await settingListPage.settingDialog.checkUseExpMap();
    await settingListPage.settingDialog.checkUseForeignKey();
    await settingListPage.settingDialog.clickSaveButton("test");
    await settingListPage.settingDialog.clickCloseButton();
}

const addSettingWithoutForceAliasColumn = async (page: Page) => {
    const settingListPage = new SettingListPage(page);
    await settingListPage.openSettingPage();
    await settingListPage.clickNewItemButton();
    
    await settingListPage.settingDialog.selectDatabaseSettingsSection();
    await settingListPage.settingDialog.setUrlField("jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9");
    await settingListPage.settingDialog.setDbTypeField("MySQL");
    await settingListPage.settingDialog.setSchemaField("gef_jdbc_tool");
    await settingListPage.settingDialog.setUsernameField("geframe");
    await settingListPage.settingDialog.setPasswordField("Kccs0000");
    await settingListPage.settingDialog.selectReservedWordsSettingsSection();
    await settingListPage.settingDialog.setPrimaryKeyField("objectID");
    await settingListPage.settingDialog.setExclusiveKeyField("ExclusiveFG");
    await settingListPage.settingDialog.setCompanyCodeField("CompanyCD");
    await settingListPage.settingDialog.setInsertedUserIdField("RegisteredPerson");
    await settingListPage.settingDialog.setInsertedDateField("RegisteredDT");
    await settingListPage.settingDialog.setUpdatedUserIdField("UpdatedPerson");
    await settingListPage.settingDialog.setUpdatedDateField("UpdatedDT");
    await settingListPage.settingDialog.selectLocaleSettingsSection();
    await settingListPage.settingDialog.setLocaleField("en");
    await settingListPage.settingDialog.setCountry1Field("en");
    await settingListPage.settingDialog.setCountry2Field("jp");
    await settingListPage.settingDialog.setCountry3Field("id");
    await settingListPage.settingDialog.setCountry4Field("zh");
    await settingListPage.settingDialog.setCountry5Field("us");
    await settingListPage.settingDialog.selectTransformSettingsSection();
    await settingListPage.settingDialog.checkUseForeignKey();
    await settingListPage.settingDialog.clickSaveButton("settingWithoutForceAliasColumn");
    await settingListPage.settingDialog.clickCloseButton();
}

const addNewInputEntityId = async (inputListPage: InputListPage) => {
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('78DAAF8551A34F40AF2EF17D388F29AC');
    await inputListPage.inputDialog.clickEntityIdRadioButton();
    await inputListPage.inputDialog.clickSaveButton();
    await inputListPage.inputDialog.clickCloseButton();
}
const addNewFalseInputEntityId = async (inputListPage: InputListPage) => {
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('78DAAF8551A34F40AF2EF17D388F29AZ');
    await inputListPage.inputDialog.clickEntityIdRadioButton();
    await inputListPage.inputDialog.clickSaveButton();
    await inputListPage.inputDialog.clickCloseButton();
}
const addNewInputDatabaseId = async (inputListPage: InputListPage) => {
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('377BB20685E5497FB5B5209DACC76432');
    await inputListPage.inputDialog.clickDatabaseIdRadioButton();
    await inputListPage.inputDialog.clickSaveButton();
    await inputListPage.inputDialog.clickCloseButton();
}
const addNewFalseInputDatabaseId = async (inputListPage: InputListPage) => {
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('377BB20685E5497FB5B5209DACC7643Z');
    await inputListPage.inputDialog.clickDatabaseIdRadioButton();
    await inputListPage.inputDialog.clickSaveButton();
    await inputListPage.inputDialog.clickCloseButton();
}

test('存在するエンティティIDを入力し、「IDの検証」ボタンで検証すると、「IDが存在する」と表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('78DAAF8551A34F40AF2EF17D388F29AC');
    await inputListPage.inputDialog.clickEntityIdRadioButton();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        await dialog.accept();
    });
    await inputListPage.inputDialog.clickCheckButton();
    await expect(page.getByText("確認中")).toBeVisible();
    await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
    expect(alertMessage).toBe('IDが存在します。');
    
});

test('存在しないエンティティIDを入力し、「IDの検証」ボタンで検証すると、「IDが存在しません」と表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('78DAAF8551A34F40AF2EF17D388F29ZZ');
    await inputListPage.inputDialog.clickEntityIdRadioButton();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        await dialog.accept();
    });
    await inputListPage.inputDialog.clickCheckButton();
    await expect(page.getByText("確認中")).toBeVisible();
    await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
    expect(alertMessage).toBe('IDが存在しません');
});

test('存在しないエンティティIDを入力し、「保存」ボタンで検証すると、「IDが存在しません。保存できません。」と表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();

    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('377BB20685E5497FB5B5209DACC7643A');
    await inputListPage.inputDialog.clickEntityIdRadioButton();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        await dialog.accept();
    });
    await inputListPage.inputDialog.clickSaveButton();
    await expect(page.getByText("確認中")).toBeVisible();
    await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
    expect(alertMessage).toBe('IDが存在しません。保存できません。');
});

test('存在するデータベースIDを入力し、「IDの検証」ボタンで検証すると、「IDが存在する」と表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('377BB20685E5497FB5B5209DACC76432');
    await inputListPage.inputDialog.clickDatabaseIdRadioButton();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        await dialog.accept();
    });
    await inputListPage.inputDialog.clickCheckButton();
    await expect(page.getByText("確認中")).toBeVisible();
    await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
    expect(alertMessage).toBe('IDが存在します。');
});

test('存在しないデータベースIDを入力し、「IDの検証」ボタンで検証すると、「IDが存在しません」と表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();

    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('377BB20685E5497FB5B5209DACC7643Z');
    await inputListPage.inputDialog.clickDatabaseIdRadioButton();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        await dialog.accept();
    });
    await inputListPage.inputDialog.clickCheckButton();
    await expect(page.getByText("確認中")).toBeVisible();
    await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
    expect(alertMessage).toBe('IDが存在しません');
});

test('存在しないデータベースIDを入力し、「保存」ボタンで検証すると、「IDが存在しません。保存できません。」と表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();

    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('377BB20685E5497FB5B5209DACC7643Z');
    await inputListPage.inputDialog.clickDatabaseIdRadioButton();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        await dialog.accept();
    });
    await inputListPage.inputDialog.clickSaveButton();
    await expect(page.getByText("確認中")).toBeVisible();
    await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
    expect(alertMessage).toBe('IDが存在しません。保存できません。');
});

test('グローバル設定に接続できない場合、「IDの検証」ボタンで検証すると「IDが存在しません」と表示されること。', async ({ page }) => {
    await addFalseSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();
    
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('78DAAF8551A34F40AF2EF17D388F29AC');
    await inputListPage.inputDialog.clickEntityIdRadioButton();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        await dialog.accept();
    });
    await inputListPage.inputDialog.clickCheckButton();
    await expect(page.getByText("確認中")).toBeVisible();
    await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
    expect(alertMessage).toBe('IDが存在しません');
});


test("変換条件を削除した後、ページを再読み込みしても、記録から削除された状態が保持されていること。", async({page}) => {
    const inputListPage = new InputListPage(page);
    await inputListPage.openInputPage();
    await inputListPage.selectTransformConditionSection();
    await addNewInputEntityId(inputListPage);
    await inputListPage.loadListInputs();
    let listInputs = await inputListPage.listInputs.count();
    let listInputsCheckboxes = await inputListPage.listInputsCheckboxes.count();
    expect(listInputs).toBe(1);
    expect(listInputsCheckboxes).toBe(1);
    await inputListPage.clickListInputsCheckbox(0)
    await inputListPage.clickDeleteButton();
    listInputs = await inputListPage.listInputs.count();
    listInputsCheckboxes = await inputListPage.listInputsCheckboxes.count();
    expect(listInputs).toBe(0);
    expect(listInputsCheckboxes).toBe(0);
})



// test('グローバル設定に接続できない場合、変換が続行できず、「選択された変換設定で接続が失敗です。」エラーメッセージが表示されること。', async ({ page }) => {
//     await addFalseSetting(page);
//     const inputListPage = new InputListPage(page);
//     await inputListPage.openInputPage();
//     await inputListPage.selectGlobalSetting('test');
//     await inputListPage.selectTransformConditionSection();
//     let alertMessage = '';
//     page.on('dialog', async (dialog) => {
//         alertMessage = dialog.message();
//         await dialog.accept();
//         expect(alertMessage).toBe('選択された変換設定で接続が失敗です。');
//     });
    
//     await inputListPage.clickTransformScriptButton();
// });

// test('特定設定が接続できない場合、「チェック」ボタンで検証すると「IDが存在しません」が表示されること。', async ({ page }) => {
//     await addFalseSetting(page);
//     await addSettingWithoutForceAliasColumn(page);
//     const inputListPage = new InputListPage(page);
//     await inputListPage.openInputPage();
//     await inputListPage.selectGlobalSetting('settingWithoutForceAliasColumn');
//     await inputListPage.selectTransformConditionSection();
    
//     await inputListPage.clickNewItemButton();
//     await inputListPage.inputDialog.setId('78DAAF8551A34F40AF2EF17D388F29AC');
//     await inputListPage.inputDialog.clickEntityIdRadioButton();
//     await inputListPage.inputDialog.clickSettingInfoExtendedLabel();
//     await inputListPage.inputDialog.selectSettingOptions('test');
    
//     let alertMessage = '';
//     page.on('dialog', async (dialog) => {
//         alertMessage = dialog.message();
//         await dialog.accept();
//     });
//     await inputListPage.inputDialog.clickCheckButton();
//     await expect(page.getByText("確認中")).toBeVisible();
//     await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
//     expect(alertMessage).toBe('IDが存在しません');
// });

test('インポートファイルの形式が一致する場合、インポートが成功し、インポートされた条件が適切に表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();
    await inputListPage.selectImportFileTemp('test-files/dao2-entity-xform(input).yml');
    await inputListPage.loadListInputs();
    const listInputs = await inputListPage.listInputs.count();
    const listInputsCheckboxes = await inputListPage.listInputsCheckboxes.count();
    expect(listInputs).toBe(3);
    expect(listInputsCheckboxes).toBe(3);
});

test('インポートファイルの形式が一致しない場合、インポートが失敗し、「エラー」が表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        expect(alertMessage).toBe('フォーマットは違います。');
    });
    await inputListPage.selectImportFileTemp('test-files/dao2-entity-xform(input)-false.yml');
    
});

// test('特定設定が接続できない場合、「チェック」ボタンで検証すると「IDが存在しません」が表示されること。2', async ({ page }) => {
//     await addSettingWithoutForceAliasColumn(page);
//     await addFalseSetting(page);
//     const inputListPage = new InputListPage(page);
//     await inputListPage.openInputPage();
//     await inputListPage.selectGlobalSetting('settingWithoutForceAliasColumn');
//     await inputListPage.selectTransformConditionSection();
//     await inputListPage.clickNewItemButton();
//     await inputListPage.inputDialog.setId('0AF1C308DE0848E9AB8FFD11D4EEC32E');
//     await inputListPage.inputDialog.clickEntityIdRadioButton();
//     await inputListPage.inputDialog.clickSettingInfoExtendedLabel();
//     await inputListPage.inputDialog.selectSettingOptions('test');
    
//     let alertMessage = '';
//     page.on('dialog', async (dialog) => {
//         alertMessage = dialog.message();
//         await dialog.accept();
//     });
//     await inputListPage.inputDialog.clickCheckButton();
//     await expect(page.getByText("確認中")).toBeVisible();
//     await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
//     expect(alertMessage).toBe('IDが存在しません');
// });

test("エンティティIDのみが入力条件である場合、変換が成功すること。	", async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();
    await addNewInputEntityId(inputListPage);
    await inputListPage.clickTransformScriptButton();
    await expect(page.getByText("エンティティID: 78DAAF8551A34F40AF2EF17D388F29AC")).toBeVisible({timeout: 1200000});
    await inputListPage.transformDialog.clickTransformButton();

    await expect(page.getByText("エンティティID: 78DAAF8551A34F40AF2EF17D388F29AC")).toBeVisible({timeout: 1200000});
    
    await inputListPage.transformDialog.clickScript("エンティティID: 78DAAF8551A34F40AF2EF17D388F29AC");
    await expect(page.locator('#xml-0 pre')).toBeVisible();
    await expect(page.locator('#xml-0 pre')).toHaveText(`<?xml version="1.0" encoding="UTF-8" standalone="yes"?> <Entity database="gef_jdbc_tool" phyName="jdbc_tool_test_fullCaseMainEntity"> <Columns> <Column name="objectID" phyName="objectID" type="Varchar" size="32"/> <Column name="StringColumn" phyName="StringColumn" type="Varchar" size="32"/> <Column name="IntColumn" phyName="IntColumn" type="Integer" size="8"/> <Column name="NStringColumn" phyName="NStringColumn" type="Varchar" size="32"/> <Column name="DateTimeColumn" phyName="DateTimeColumn" type="Date"/> <Column name="YMColumn" phyName="YMColumn" type="Varchar" size="6"/> <Column name="CurrencyColumn" phyName="CurrencyColumn" type="Double" size="32" scale="2"/> <Column name="LongColumn" phyName="LongColumn" type="Long" size="32"/> <Column name="CompanyCD" phyName="CompanyCD" type="Varchar" size="32"/> <Column name="ExclusiveFG" phyName="ExclusiveFG" type="Varchar" size="32" exclusiveKey="true"/> <Column name="RegisteredPerson" phyName="RegisteredPerson" type="Varchar" size="32"/> <Column name="RegisteredDT" phyName="RegisteredDT" type="Date" generate="RegisteredDate"/> <Column name="UpdatedPerson" phyName="UpdatedPerson" type="Varchar" size="32"/> <Column name="UpdatedDT" phyName="UpdatedDT" type="Date" generate="UpdatedDate"/> </Columns> <PrimaryKey> <ReferenceColumn refName="objectID"/> </PrimaryKey> <UniqueKeys> <UniqueKey name="MultipleColumnUK"> <ReferenceColumn refName="StringColumn"/> <ReferenceColumn refName="IntColumn"/> <ReferenceColumn refName="NStringColumn"/> </UniqueKey> <UniqueKey name="OtherUKName"> <ReferenceColumn refName="IntColumn"/> <ReferenceColumn refName="NStringColumn"/> <ReferenceColumn refName="DateTimeColumn"/> <ReferenceColumn refName="YMColumn"/> </UniqueKey> <UniqueKey name="SingleColumnUK"> <ReferenceColumn refName="StringColumn"/> </UniqueKey> </UniqueKeys> <ForeignKeys> <ForeignKey name="RightJoinRefColumnAndConstValue" refEntity="jdbc_tool_test_fullCaseRightJoinEntity"> <JoinColumn refName="NStringColumnRight" srcName="NStringColumn"/> <JoinColumn refName="StringColumnRight" constValue="7001"/> </ForeignKey> <ForeignKey name="InnerJoinRefCoulumnAndConstValue" refEntity="jdbc_tool_test_fullCaseInnerJoinEntity"> <JoinColumn refName="StringColumnInner" srcName="StringColumn"/> <JoinColumn refName="NStringColumnInner" constValue="9001"/> </ForeignKey> <ForeignKey name="LeftJoinRefColumnAndConstValue" refEntity="jdbc_tool_test_fullCaseLeftJoinEntity"> <JoinColumn refName="IntColumnLeft" srcName="IntColumn"/> <JoinColumn refName="LongColumnLeft" constValue="8001"/> </ForeignKey> </ForeignKeys> </Entity>`);
    
});


test('データベースIDのみが入力条件である場合、変換が成功すること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();
    await addNewInputDatabaseId(inputListPage);
    await inputListPage.clickTransformScriptButton();
    await expect(page.getByText("処理中")).toBeHidden({timeout: 1200000});
    await expect(page.getByText("エンティティID: 008F7518567E4B75AB7EFC4919939E31(データベースID: 377BB20685E5497FB5B5209DACC76432)")).toBeVisible({timeout: 1200000});
    await expect(page.getByText("1 / 2ページ（1-10件 / 19件）")).toBeVisible();
    await inputListPage.transformDialog.clickTransformButton();

    await expect(page.getByText("エンティティID: 008F7518567E4B75AB7EFC4919939E31(データベースID: 377BB20685E5497FB5B5209DACC76432)")).toBeVisible({timeout: 1200000});
    
    await inputListPage.transformDialog.clickScript("エンティティID: 008F7518567E4B75AB7EFC4919939E31(データベースID: 377BB20685E5497FB5B5209DACC76432)");
    await expect(page.getByText("1 / 2ページ（1-10件 / 19件）")).toBeVisible({timeout: 1200000});
});

test('インポートされたエンティティIDとデータベースIDがデータソースに存在しない場合、変換後の結果が空になること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();
    await inputListPage.selectImportFileTemp('test-files/dao2-entity-xform(input)-db-entity-false.yml');
    await page.keyboard.press('Escape');
    await inputListPage.loadListInputs();
    const listInputs = await inputListPage.listInputs.count();
    const listInputsCheckboxes = await inputListPage.listInputsCheckboxes.count();
    expect(listInputs).toBe(2);
    expect(listInputsCheckboxes).toBe(2);
    await inputListPage.clickTransformScriptButton();
    await expect(page.getByText("エンティティID: 2726BD6AA6254A1590612E092A91ACEZ")).toBeVisible({timeout: 1200000});
    await expect(page.getByText("1件の仮想表の取得が完了しました")).toBeVisible();
    await expect(page.getByText("1件の仮想表の取得が完了しました")).toBeHidden({timeout: 1200000});
    await inputListPage.transformDialog.clickTransformButton();
    await expect(page.getByText("変換中。。。").first()).not.toBeVisible({timeout: 1200000});
    await expect(page.getByText("エンティティID: 2726BD6AA6254A1590612E092A91ACEZ")).not.toBeVisible({timeout: 1200000});
});

test('インポートされたエンティティIDとデータベースIDがデータソースに存在する場合、変換後の結果が適切に表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();
    await inputListPage.selectImportFileTemp('test-files/dao2-entity-xform(input)-db-entity-true.yml');
    await page.keyboard.press('Escape');
    await inputListPage.loadListInputs();
    const listInputs = await inputListPage.listInputs.count();
    const listInputsCheckboxes = await inputListPage.listInputsCheckboxes.count();
    expect(listInputs).toBe(2);
    expect(listInputsCheckboxes).toBe(2);
    await inputListPage.clickTransformScriptButton();
    await expect(page.getByText("1件の仮想表の取得が完了しました")).toBeVisible();
    await expect(page.getByText("1件の仮想表の取得が完了しました")).toBeHidden({timeout: 1200000});
    await expect(page.getByText("1 / 2ページ（1-10件 / 19件）")).toBeVisible();
    await expect(page.getByText("エンティティID: 2726BD6AA6254A1590612E092A91ACE4")).toBeVisible({timeout: 1200000});
    await expect(page.getByText("エンティティID: 008F7518567E4B75AB7EFC4919939E31(データベースID: 377BB20685E5497FB5B5209DACC76432)")).toBeVisible({timeout: 1200000});
    
    await inputListPage.transformDialog.clickTransformButton();
    await expect(page.getByText("エンティティID: 008F7518567E4B75AB7EFC4919939E31(データベースID: 377BB20685E5497FB5B5209DACC76432)")).toBeVisible({timeout: 1200000});
    await expect(page.getByText("1 / 2ページ（1-10件 / 19件）")).toBeVisible({timeout: 1200000});
});