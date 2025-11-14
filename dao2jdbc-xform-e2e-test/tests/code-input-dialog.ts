import { Locator, Page } from "@playwright/test";

export class InputDialogPage {
    readonly page: Page;
    readonly closeButton: Locator;
    readonly saveButton: Locator;
    readonly checkButton: Locator;
    readonly idField: Locator;
    readonly categoryIdRadioButton: Locator;
    readonly veIdRadioButton: Locator;
    readonly selectCrudTypeCheckbox: Locator;
    readonly deleteCrudTypeCheckbox: Locator;
    readonly insertCrudTypeCheckbox: Locator;
    readonly updateCrudTypeCheckbox: Locator;
    readonly additionalInfoExtendedLabel: Locator;
    readonly settingInfoExtendedLabel: Locator;
    readonly settingOptions: Locator;
    

    constructor(page: Page) {
        this.page = page;
        this.closeButton = page.getByRole('button', { name: 'close' });
        this.saveButton = page.getByRole('button', { name: '保存' });
        this.checkButton = page.getByRole('button', { name: 'IDの検証' });
        this.idField = page.getByRole('textbox', { name: 'ID' });
        this.categoryIdRadioButton = page.getByRole('radio', { name: 'カテゴリID' });
        this.veIdRadioButton = page.getByRole('radio', { name: '仮想表ID' });
        this.selectCrudTypeCheckbox = page.getByRole('checkbox', { name: 'SELECT' });
        this.deleteCrudTypeCheckbox = page.getByRole('checkbox', { name: 'DELETE' });
        this.insertCrudTypeCheckbox = page.getByRole('checkbox', { name: 'INSERT' });
        this.updateCrudTypeCheckbox = page.getByRole('checkbox', { name: 'UPDATE' });
        this.additionalInfoExtendedLabel = page.locator('div').filter({ hasText: '追加情報' }).nth(3);
        this.settingInfoExtendedLabel = page.getByRole('treeitem', { name: '設定情報' }).locator('div').first();
        this.settingOptions = page.getByTestId('selected-setting');
    }

    async clickCloseButton() {
        await this.closeButton.click();
    }
    async clickSaveButton() {
        await this.saveButton.click();
    }
    async clickCheckButton() {
        await this.checkButton.click();
    }
    async setId(id: string) {
        await this.idField.fill(id);
    }
    async checkSelectCrudTypeCheckbox() {
        await this.selectCrudTypeCheckbox.check();
    }
    async checkDeleteCrudTypeCheckbox() {
        await this.deleteCrudTypeCheckbox.check();
    }
    async checkInsertCrudTypeCheckbox() {
        await this.insertCrudTypeCheckbox.check();
    }
    async checkUpdateCrudTypeCheckbox() {
        await this.updateCrudTypeCheckbox.check();
    }
    async clickAdditionalInfoExtendedLabel() {
        await this.additionalInfoExtendedLabel.click();
    }
    async clickSettingInfoExtendedLabel() {
        await this.settingInfoExtendedLabel.click();
    }
    async clickCategoryIdRadioButton() {
        await this.categoryIdRadioButton.click();
    }
    async clickVeIdRadioButton() {
        await this.veIdRadioButton.click();
    }
    async selectSettingOptions(name: string) {
        await this.settingOptions.click();
        await this.page.getByRole('option', { name: name, exact: true }).click()
    }

}