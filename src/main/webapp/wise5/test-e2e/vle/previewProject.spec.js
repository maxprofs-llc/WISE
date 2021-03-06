'use strict';

var _protractor = require('protractor');

var _common = require('../common.js');

var common = _interopRequireWildcard(_common);

var _vlePage = require('../vlePage.js');

var _vlePage2 = _interopRequireDefault(_vlePage);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj.default = obj; return newObj; } }

describe('WISE5 Student VLE Preview', function () {

  function clickOnPageBody() {
    (0, _protractor.element)(by.xpath('//body')).click();
  }

  beforeEach(function () {
    var vle = new _vlePage2.default();
    _protractor.browser.get('http://localhost:8080/wise/project/demo#/vle/node1');
    _protractor.browser.wait(function () {
      return vle.nodeDropDownMenu.isPresent();
    }, 5000, 'VLE didn\'t load properly').then(function () {
      expect(_protractor.browser.getTitle()).toEqual('WISE');
    });
  });

  it('should show first step and default UI elements on the page', function () {
    var vle = new _vlePage2.default();
    vle.nodeSelectMenuShouldSay('1.1: HTML Step');
    common.shouldBePresent(vle.prevButton, vle.nextButton, vle.closeNodeButton, vle.accountButton, vle.accountMenu, vle.notificationButton, vle.notificationMenu);
    common.shouldBeHidden(vle.accountMenu, vle.notificationMenu);
    var nodeContent = (0, _protractor.element)(by.cssContainingText('.node-content', 'This is a step where authors can enter their own html.'));
    common.shouldBePresent(nodeContent);
    common.shouldBeEnabled(vle.nextButton);
    common.shouldBeDisabled(vle.prevButton);
  });

  it('should navigate next and previous steps using the buttons', function () {
    var vle = new _vlePage2.default();
    vle.goToNextStep();
    common.urlShouldBe('http://localhost:8080/wise/project/demo#/vle/node2');
    vle.nodeSelectMenuShouldSay('1.2: Open Response Step');
    common.shouldBeEnabled(vle.prevButton, vle.nextButton);
    var nodeContent = (0, _protractor.element)(by.cssContainingText('.node-content', 'This is a step where students enter text.'));
    common.shouldBePresent(nodeContent);

    vle.goToNextStep();
    common.urlShouldBe('http://localhost:8080/wise/project/demo#/vle/node3');
    vle.nodeSelectMenuShouldSay('1.3: Open Response Step Auto Graded');
    nodeContent = (0, _protractor.element)(by.cssContainingText('.node-content', 'Explain how the sun helps animals survive.'));
    common.shouldBePresent(nodeContent);

    vle.goToPreviousStep();
    common.urlShouldBe('http://localhost:8080/wise/project/demo#/vle/node2');
    vle.nodeSelectMenuShouldSay('1.2: Open Response Step');
  });

  it('should allow user to jump to a step using the navigation drop-down menu', function () {
    var vle = new _vlePage2.default();
    vle.openDropDownMenu();
    _protractor.element.all(by.repeater("item in stepToolsCtrl.idToOrder | toArray | orderBy : 'order'")).then(function (stepSelectOptions) {
      expect(stepSelectOptions[1].element(by.css('.node-select__text')).getText()).toBe("1.1: HTML Step");
      expect(stepSelectOptions[7].element(by.css('.node-select__text')).getText()).toBe("1.7: Challenge Question Step");
      stepSelectOptions[7].element(by.css('.node-select__text')).click();
      common.urlShouldBe('http://localhost:8080/wise/project/demo#/vle/node7');
    });
  });

  it('should display the group view and allow user to collapse/expand group navitems', function () {
    var vle = new _vlePage2.default();
    vle.toggleConstraints();
    vle.closeNode();
    common.urlShouldBe('http://localhost:8080/wise/project/demo#/vle/group1');

    _protractor.element.all(by.repeater('id in navCtrl.rootNode.ids')).then(function (groupNavItems) {
      var activity1 = groupNavItems[0];
      var activity2 = groupNavItems[1];

      expect(activity1.element(by.className('md-title')).getText()).toEqual('1: Example Steps');
      expect(activity2.element(by.className('md-title')).getText()).toEqual('2: Example Features');

      // activity 1 should be expanded, Activity 2 should be collapsed
      expect(common.hasClass(activity1, 'expanded')).toBe(true);
      expect(common.hasClass(activity2, 'expanded')).toBe(false);

      // check for completion icons for steps in Activity 1
      activity1.all(by.repeater('childId in navitemCtrl.item.ids')).then(function (stepNavItems) {

        // step 1.1 should be completed because it's an HTML step and we visited it
        // (the previous step we were on) should be highlighted because we came from it
        expect(stepNavItems[0].getText()).toBe('school\n1.1: HTML Step check_circle');
        expect(common.hasClass(stepNavItems[0], 'prev')).toBe(true);
        expect(stepNavItems[0].element(by.cssContainingText('.material-icons', 'check_circle')).isPresent()).toBeTruthy();

        // step 1.2 should not be completed yet
        expect(stepNavItems[1].getText()).toBe('school\n1.2: Open Response Step');
        expect(stepNavItems[1].element(by.cssContainingText('.material-icons', 'check_circle')).isPresent()).toBeFalsy();

        // step 1.7 should not be completed yet
        expect(stepNavItems[6].getText()).toBe('school\n1.7: Challenge Question Step');
        expect(stepNavItems[6].element(by.cssContainingText('.material-icons', 'check_circle')).isPresent()).toBeFalsy();
      });

      // activity 2 should not be expanded yet, so expand it
      activity2.element(by.className('nav-item--card__content')).click();
      expect(common.hasClass(activity2, 'expanded')).toBe(true);
      expect(common.hasClass(activity1, 'expanded')).toBe(true);

      // check that steps in activity 2 displays the step title and icon
      activity2.all(by.repeater('childId in navitemCtrl.item.ids')).then(function (stepNavItems) {
        expect(stepNavItems[0].getText()).toBe('school\n2.1: Show Previous Work 1');
        expect(stepNavItems[1].getText()).toBe('school\n2.2: Show Previous Work 2');
        expect(stepNavItems[2].getText()).toBe('school\n2.3: Import Work 1');
        // go to step 2.3.
        stepNavItems[2].element(by.tagName('button')).click();
        common.urlShouldBe('http://localhost:8080/wise/project/demo#/vle/node22');
      });
    });
  });

  it('should allow user to jump to a step by changing the URL path', function () {
    var vle = new _vlePage2.default();
    // the user changes the URL
    _protractor.browser.get('http://localhost:8080/wise/project/demo#/vle/node11');
    _protractor.browser.wait(function () {
      return vle.nodeDropDownMenu.isPresent();
    }, 5000, 'VLE didn\'t load properly').then(function () {
      vle.nodeSelectMenuShouldSay('1.11: Draw Step');
    });
    _protractor.browser.get('http://localhost:8080/wise/project/demo#/vle/node14');
    _protractor.browser.wait(function () {
      return vle.nodeDropDownMenu.isPresent();
    }, 5000, 'VLE didn\'t load properly').then(function () {
      vle.nodeSelectMenuShouldSay('1.15: Table Step');
    });
  });

  it('should allow preview user to view the account menu', function () {
    var vle = new _vlePage2.default();
    vle.openAccountMenu();
    common.shouldBeDisplayed(vle.accountMenu);

    // account menu should have the preview user account icon and the exit and sign out buttons
    _protractor.element.all(by.repeater('username in themeCtrl.workgroupUsernames')).then(function (workgroupNames) {
      expect(workgroupNames[0].getText()).toBe('Preview Team');
    });

    common.shouldBePresent(vle.exitButton, vle.logOutButton);

    _protractor.browser.actions().sendKeys(protractor.Key.ESCAPE).perform();
    common.shouldBeHidden(vle.accountMenu);

    vle.openAccountMenu();
    common.shouldBeDisplayed(vle.accountMenu);

    clickOnPageBody();
    common.shouldBeHidden(vle.accountMenu);
  });

  it('should allow preview user to view the notification menu', function () {
    var vle = new _vlePage2.default();
    vle.openNotificationMenu();
    common.shouldBeDisplayed(vle.notificationMenu);

    // notification menu should have the Alerts title and say that there are no alerts.
    var notificationDialogTitle = (0, _protractor.element)(by.xpath('//md-toolbar/span/span[@translate="notificationsTitle"]'));
    expect(notificationDialogTitle.isDisplayed()).toBeTruthy();
    expect(notificationDialogTitle.getText()).toEqual("Alerts");

    var notificationDialogContent = (0, _protractor.element)(by.xpath('//md-content/div/span[@translate="noAlerts"]'));
    expect(notificationDialogContent.isDisplayed()).toBeTruthy();
    expect(notificationDialogContent.getText()).toEqual("Hi there! You currently have no alerts.");

    _protractor.browser.actions().sendKeys(protractor.Key.ESCAPE).perform();
    common.shouldBeHidden(vle.notificationMenu);

    vle.openNotificationMenu();
    common.shouldBeDisplayed(vle.notificationMenu);

    clickOnPageBody();
    common.shouldBeHidden(vle.notificationMenu);
  });
});
//# sourceMappingURL=previewProject.spec.js.map
