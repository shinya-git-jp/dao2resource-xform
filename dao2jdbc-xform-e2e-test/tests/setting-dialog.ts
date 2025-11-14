import { Locator, Page } from "@playwright/test";

export class SettingDialogPage {
    readonly page: Page;
    readonly closeButton: Locator;
    readonly saveButton: Locator;
    readonly importButton: Locator;
    readonly importFileTemp: Locator;
    readonly saveNameField: Locator;
    readonly transformEncodingField: Locator;
    readonly queryModeSelectField: Locator;
    readonly forceAliasColumnCheckBox: Locator;
    readonly useExpMapCheckbox: Locator;
    readonly useForeignKeyCheckbox: Locator;
    readonly urlField: Locator;
    readonly dbTypeField: Locator;
    readonly schemaField: Locator;
    readonly usernameField: Locator;
    readonly passwordField: Locator;
    readonly localeField: Locator;
    readonly country1Field: Locator;
    readonly country2Field: Locator;
    readonly country3Field: Locator;
    readonly country4Field: Locator;
    readonly country5Field: Locator;
    readonly primaryKeyField: Locator;
    readonly exclusiveKeyField: Locator;
    readonly companyCodeField: Locator;
    readonly insertedUserIdField: Locator;
    readonly insertedDateField: Locator;
    readonly updatedUserIdField: Locator;
    readonly updatedDateField: Locator;
    readonly checkConnectionButton: Locator;
    readonly databaseSettingsSection: Locator;
    readonly localeSettingsSection: Locator;
    readonly reservedWordsSettingsSection: Locator
    readonly transformSettingsSection: Locator;


    constructor(page: Page) {
        this.page = page;
        this.closeButton = page.getByRole('button', { name: 'close' });
        this.saveButton = page.getByRole('button', { name: '保存' });
        this.importButton = page.getByRole('button', { name: 'インポート' });
        this.saveNameField = page.getByRole('textbox', { name: '設定名' });
        this.transformEncodingField = page.getByRole('textbox', { name: 'ダウンロードされたファイルのエンコーディング' });
        this.queryModeSelectField = page.getByRole('checkbox', { name: 'SQL処理にエンティティ定義情報を使用する' });
        this.forceAliasColumnCheckBox = page.getByRole('checkbox', { name: '項目のエイリアスを仮想項目名に上書きする' });
        this.useExpMapCheckbox = page.getByRole('checkbox', { name: '引数の値をバインドする際にMapを使用するかどうか' });
        this.useForeignKeyCheckbox = page.getByRole('checkbox', { name: 'エンティティ定義の外部キーを使用する' });
        this.importFileTemp = page.getByTestId('fileInputSettingDialog');
        this.urlField = page.getByRole('textbox', {name: "URL"});
        this.dbTypeField = page.getByText('Oracle');
        this.schemaField = page.getByRole('textbox', { name: 'Scheme' });
        this.usernameField = page.getByRole('textbox', { name: 'Username' });
        this.passwordField = page.getByRole('textbox', { name: 'Password' });
        this.localeField =  page.getByRole('textbox', { name: '使用する言語' });
        this.country1Field = page.getByRole('textbox', { name: 'country1の言語' });
        this.country2Field = page.getByRole('textbox', { name: 'country2の言語' });
        this.country3Field = page.getByRole('textbox', { name: 'country3の言語' });
        this.country4Field = page.getByRole('textbox', { name: 'country4の言語' });
        this.country5Field = page.getByRole('textbox', { name: 'country5の言語' });
        this.primaryKeyField = page.getByRole('textbox', { name: 'Primaryキーカラム' });
        this.exclusiveKeyField = page.getByRole('textbox', { name: 'Exclusiveキーカラム' });
        this.companyCodeField = page.getByRole('textbox', { name: '会社コードカラム' });
        this.insertedUserIdField = page.getByRole('textbox', { name: '登録者IDカラム' });
        this.insertedDateField = page.getByRole('textbox', { name: '登録日付カラム' });
        this.updatedDateField = page.getByRole('textbox', { name: '更新者IDカラム' });
        this.updatedUserIdField = page.getByRole('textbox', { name: '更新日付カラム' });
        this.checkConnectionButton = page.getByRole('button', { name: '接続テスト' });
        this.databaseSettingsSection = page.getByRole('tab', { name: 'データベース設定' });
        this.localeSettingsSection = page.getByRole('tab', { name: '言語設定' });
        this.reservedWordsSettingsSection = page.getByRole('tab', { name: '予約語設定' });
        this.transformSettingsSection = page.getByRole('tab', { name: '出力設定' });
    }

    async clickCloseButton() {
        await this.closeButton.click();
    }
    async clickSaveButton(name: string) {
        const saveNameFieldListener = async (dialog: any) => {
            await dialog.accept(name);
            this.page.off('dialog', saveNameFieldListener);
        }
        this.page.on('dialog', saveNameFieldListener);
        await this.saveButton.click();
    }
    async clickImportButton() {
        await this.importButton.click();
    }
    async selectImportFileTemp(filePath: string) {
        await this.importFileTemp.setInputFiles(filePath);
    }
    async setTransformEncoding(encoding: string) {
        await this.transformEncodingField.fill(encoding);
    }
    async checkQueryMode() {
        await this.queryModeSelectField.check();
    }
    async uncheckQueryMode() {
        await this.queryModeSelectField.uncheck();
    }
    async checkForceAliasColumn() {
        await this.forceAliasColumnCheckBox.check();
    }
    async uncheckForceAliasColumn() {
        await this.forceAliasColumnCheckBox.uncheck();
    }
    async checkUseExpMap() {
        await this.useExpMapCheckbox.check();
    }
    async uncheckUseExpMap() {
        await this.useExpMapCheckbox.uncheck();
    }
    async checkUseForeignKey() {
        await this.useForeignKeyCheckbox.check();
    }
    async uncheckUseForeignKey() {
        await this.useForeignKeyCheckbox.uncheck();
    }
    async isSaveButtonEnabled() {
        return await this.saveButton.isEnabled();
    }
    async isImportButtonEnabled() {
        return await this.importButton.isEnabled();
    }
    async isCloseButtonEnabled() {
        return await this.closeButton.isEnabled();
    }
    async isSaveNameFieldEnabled() {
        return await this.saveNameField.isEnabled();
    }
    async isTransformEncodingFieldEnabled() {
        return await this.transformEncodingField.isEnabled();
    }
    async isQueryModeSelectFieldEnabled() {
        return await this.queryModeSelectField.isEnabled();
    }
    async isForceAliasColumnCheckBoxEnabled() {
        return await this.forceAliasColumnCheckBox.isEnabled();
    }
    async isUseExpMapCheckBoxEnabled() {
        return await this.useExpMapCheckbox.isEnabled();
    }
    async isUseForeignKeyCheckBoxEnabled() {
        return await this.useForeignKeyCheckbox.isEnabled();
    }
    async setUrlField(url: string) {
        await this.urlField.fill(url);
    }
    async setDbTypeField(dbType: string) {
        await this.dbTypeField.click();
        await this.page.getByRole('option', { name: dbType }).click();
    }
    async setSchemaField(schema: string) {
        await this.schemaField.fill(schema);
    }
    async setUsernameField(username: string) {
        await this.usernameField.fill(username);
    }
    async setPasswordField(password: string) {
        await this.passwordField.fill(password);
    }
    async setLocaleField(locale: string) {
        await this.localeField.fill(locale);
    } 
    async setCountry1Field(country1: string) {
        await this.country1Field.fill(country1);
    }
    async setCountry2Field(country2: string) {
        await this.country2Field.fill(country2);
    }
    async setCountry3Field(country3: string) {
        await this.country3Field.fill(country3);
    }
    async setCountry4Field(country4: string) {
        await this.country4Field.fill(country4);
    }
    async setCountry5Field(country5: string) {
        await this.country5Field.fill(country5);
    }
    async setPrimaryKeyField(primaryKeyField: string) {
        await this.primaryKeyField.fill(primaryKeyField);
    }
    async setExclusiveKeyField(exclusiveKeyField: string) {
        await this.exclusiveKeyField.fill(exclusiveKeyField);
    }
    async setCompanyCodeField(companyCode: string) {
        await this.companyCodeField.fill(companyCode);
    }
    async setInsertedUserIdField(insertedUserId: string) {
        await this.insertedUserIdField.fill(insertedUserId);
    }
    async setInsertedDateField(insertedDateField: string) {
        await this.insertedDateField.fill(insertedDateField);
    }
    async setUpdatedUserIdField(updatedUserIdField: string) {
        await this.updatedUserIdField.fill(updatedUserIdField);
    }
    async setUpdatedDateField(updatedDate: string) {
        await this.updatedDateField.fill(updatedDate);
    }
    async clickConnectionTestButton() {
        await this.checkConnectionButton.click();
    }
    async selectDatabaseSettingsSection() {
        await this.databaseSettingsSection.click();
    }
    async selectLocaleSettingsSection() {
        await this.localeSettingsSection.click();
    }
    async selectReservedWordsSettingsSection() {
        await this.reservedWordsSettingsSection.click();
    }
    async selectTransformSettingsSection() {
        await this.transformSettingsSection.click();
    }
}