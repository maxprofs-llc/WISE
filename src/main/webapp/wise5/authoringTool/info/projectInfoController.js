'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var ProjectInfoController = function () {
  function ProjectInfoController($filter, $mdDialog, $rootScope, $state, $stateParams, $scope, $timeout, ConfigService, ProjectService, UtilService) {
    _classCallCheck(this, ProjectInfoController);

    this.$filter = $filter;
    this.$mdDialog = $mdDialog;
    this.$rootScope = $rootScope;
    this.$state = $state;
    this.$stateParams = $stateParams;
    this.$scope = $scope;
    this.$timeout = $timeout;
    this.ConfigService = ConfigService;
    this.ProjectService = ProjectService;
    this.UtilService = UtilService;
    this.$translate = this.$filter('translate');

    this.metadata = this.ProjectService.getProjectMetadata();
    this.metadataAuthoring = this.ConfigService.getConfigParam('projectMetadataSettings');
    this.projectIcons = [];
    this.projectIcon = '';
    this.isEditingProjectIcon = false;
    this.isShowProjectIcon = false;
    this.isShowProjectIconError = false;
    this.isShowProjectIconLoading = false;
    this.loadProjectIcon();
    this.processMetadata();
    this.registerListeners();
  }

  _createClass(ProjectInfoController, [{
    key: 'registerListeners',
    value: function registerListeners() {
      var _this = this;

      this.$scope.$on('assetSelected', function (event, args) {
        if (args.target === 'projectIcon') {
          _this.setCustomProjectIcon(args.assetItem.fileName);
          _this.$mdDialog.hide();
        }
      });
    }
  }, {
    key: 'processMetadata',
    value: function processMetadata() {
      if (this.metadataAuthoring != null) {
        var _iteratorNormalCompletion = true;
        var _didIteratorError = false;
        var _iteratorError = undefined;

        try {
          for (var _iterator = this.metadataAuthoring.fields[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
            var field = _step.value;

            this.processMetadataAuthoringField(field);
          }
        } catch (err) {
          _didIteratorError = true;
          _iteratorError = err;
        } finally {
          try {
            if (!_iteratorNormalCompletion && _iterator.return) {
              _iterator.return();
            }
          } finally {
            if (_didIteratorError) {
              throw _iteratorError;
            }
          }
        }
      }
    }
  }, {
    key: 'processMetadataAuthoringField',
    value: function processMetadataAuthoringField(field) {
      if (field != null) {
        if (field.type === 'checkbox') {
          this.processMetadataAuthoringFieldCheckbox(field);
        } else if (field.type === 'radio') {
          // do nothing. Radio buttons work automatically
        }
      }
    }
  }, {
    key: 'processMetadataAuthoringFieldCheckbox',
    value: function processMetadataAuthoringFieldCheckbox(field) {
      var metadataField = this.metadata[field.key];
      if (metadataField != null && field.choices != null) {
        field.choicesMapping = {};
        var _iteratorNormalCompletion2 = true;
        var _didIteratorError2 = false;
        var _iteratorError2 = undefined;

        try {
          for (var _iterator2 = field.choices[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
            var choice = _step2.value;

            if (choice != null) {
              field.choicesMapping[choice] = this.hasUserCheckedThisMetadataField(metadataField, choice);
            }
          }
        } catch (err) {
          _didIteratorError2 = true;
          _iteratorError2 = err;
        } finally {
          try {
            if (!_iteratorNormalCompletion2 && _iterator2.return) {
              _iterator2.return();
            }
          } finally {
            if (_didIteratorError2) {
              throw _iteratorError2;
            }
          }
        }
      }
    }
  }, {
    key: 'hasUserCheckedThisMetadataField',
    value: function hasUserCheckedThisMetadataField(metadataField, choice) {
      var userHasCheckedThisMetadataField = false;
      var _iteratorNormalCompletion3 = true;
      var _didIteratorError3 = false;
      var _iteratorError3 = undefined;

      try {
        for (var _iterator3 = metadataField[Symbol.iterator](), _step3; !(_iteratorNormalCompletion3 = (_step3 = _iterator3.next()).done); _iteratorNormalCompletion3 = true) {
          var metadataFieldChoice = _step3.value;

          if (metadataFieldChoice != null && metadataFieldChoice == choice) {
            userHasCheckedThisMetadataField = true;
            break;
          }
        }
      } catch (err) {
        _didIteratorError3 = true;
        _iteratorError3 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion3 && _iterator3.return) {
            _iterator3.return();
          }
        } finally {
          if (_didIteratorError3) {
            throw _iteratorError3;
          }
        }
      }

      return userHasCheckedThisMetadataField;
    }

    // returns the choice text that is appropriate for user's locale

  }, {
    key: 'getMetadataChoiceText',
    value: function getMetadataChoiceText(choice) {
      var choiceText = choice;
      var userLocale = this.ConfigService.getLocale();
      var i18nMapping = this.metadataAuthoring.i18n;
      var i18nMappingContainingChoiceTextArray = Object.values(i18nMapping).filter(function (onei18nMapping) {
        return Object.values(onei18nMapping).indexOf(choice) != -1;
      });
      if (i18nMappingContainingChoiceTextArray != null && i18nMappingContainingChoiceTextArray.length > 0) {
        // shouldn't be more than one, but if so, use the first one we find
        var i18nMappingContainingChoiceText = i18nMappingContainingChoiceTextArray[0];
        if (i18nMappingContainingChoiceText[userLocale] != null) {
          choiceText = i18nMappingContainingChoiceText[userLocale];
        }
      }
      return choiceText;
    }
  }, {
    key: 'metadataChoiceIsChecked',
    value: function metadataChoiceIsChecked(metadataField, choice) {
      return this.getMetadataChoiceText(this.metadata[metadataField.key]) == this.getMetadataChoiceText(choice);
    }
  }, {
    key: 'metadataCheckboxClicked',
    value: function metadataCheckboxClicked(metadataField, choice) {
      var checkedChoices = [];
      var choices = metadataField.choices;
      var _iteratorNormalCompletion4 = true;
      var _didIteratorError4 = false;
      var _iteratorError4 = undefined;

      try {
        for (var _iterator4 = choices[Symbol.iterator](), _step4; !(_iteratorNormalCompletion4 = (_step4 = _iterator4.next()).done); _iteratorNormalCompletion4 = true) {
          var _choice = _step4.value;

          var isChoiceChecked = metadataField.choicesMapping[_choice];
          if (isChoiceChecked) {
            checkedChoices.push(this.getMetadataChoiceText(_choice));
          }
        }
      } catch (err) {
        _didIteratorError4 = true;
        _iteratorError4 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion4 && _iterator4.return) {
            _iterator4.return();
          }
        } finally {
          if (_didIteratorError4) {
            throw _iteratorError4;
          }
        }
      }

      this.metadata[metadataField.key] = checkedChoices;
      this.ProjectService.saveProject();
    }
  }, {
    key: 'metadataRadioClicked',
    value: function metadataRadioClicked(metadataField, choice) {
      this.metadata[metadataField.key] = this.getMetadataChoiceText(choice);
      this.ProjectService.saveProject();
    }
  }, {
    key: 'getFeaturedProjectIcons',
    value: function getFeaturedProjectIcons() {
      var _this2 = this;

      this.ProjectService.getFeaturedProjectIcons().then(function (featuredProjectIcons) {
        _this2.projectIcons = featuredProjectIcons;
      });
    }
  }, {
    key: 'setFeaturedProjectIcon',
    value: function setFeaturedProjectIcon(projectIcon) {
      var _this3 = this;

      this.ProjectService.setFeaturedProjectIcon(projectIcon).then(function () {
        _this3.projectIcon = 'wise5/authoringTool/projectIcons/' + projectIcon;
        _this3.showProjectIcon();
        _this3.closeEditProjectIconMode();
      });
    }
  }, {
    key: 'chooseCustomProjectIcon',
    value: function chooseCustomProjectIcon() {
      var params = {
        isPopup: true,
        target: 'projectIcon'
      };
      this.$rootScope.$broadcast('openAssetChooser', params);
    }
  }, {
    key: 'setCustomProjectIcon',
    value: function setCustomProjectIcon(projectIcon) {
      var _this4 = this;

      this.showProjectIconLoading();
      this.ProjectService.setCustomProjectIcon(projectIcon).then(function () {
        _this4.loadProjectIconAfterTimeout();
      });
    }

    /*
     * Load the project_thumb.png after a timeout to allow time for the image to be updated on the server
     * and browser. This is to prevent the browser from displaying the previous project_thumb.png.
     */

  }, {
    key: 'loadProjectIconAfterTimeout',
    value: function loadProjectIconAfterTimeout() {
      var _this5 = this;

      this.$timeout(function () {
        _this5.loadProjectIcon();
        _this5.closeEditProjectIconMode();
      }, 3000);
    }
  }, {
    key: 'loadProjectIcon',
    value: function loadProjectIcon() {
      var _this6 = this;

      this.projectIcon = this.ConfigService.getConfigParam('projectBaseURL') + 'assets/project_thumb.png?timestamp=' + new Date().getTime();
      var image = new Image();
      image.onerror = function () {
        _this6.showProjectIconError();
      };
      image.onload = function () {
        _this6.showProjectIcon();
      };
      image.src = this.projectIcon;
    }
  }, {
    key: 'toggleEditProjectIconMode',
    value: function toggleEditProjectIconMode() {
      this.isEditingProjectIcon = !this.isEditingProjectIcon;
      if (this.isEditingProjectIcon) {
        this.getFeaturedProjectIcons();
      }
    }
  }, {
    key: 'closeEditProjectIconMode',
    value: function closeEditProjectIconMode() {
      this.isEditingProjectIcon = false;
    }
  }, {
    key: 'showProjectIcon',
    value: function showProjectIcon() {
      this.isShowProjectIcon = true;
      this.isShowProjectIconError = false;
      this.isShowProjectIconLoading = false;
    }
  }, {
    key: 'showProjectIconError',
    value: function showProjectIconError() {
      this.isShowProjectIcon = false;
      this.isShowProjectIconError = true;
      this.isShowProjectIconLoading = false;
    }
  }, {
    key: 'showProjectIconLoading',
    value: function showProjectIconLoading() {
      this.isShowProjectIcon = false;
      this.isShowProjectIconError = false;
      this.isShowProjectIconLoading = true;
    }
  }, {
    key: 'save',
    value: function save() {
      this.ProjectService.saveProject();
    }
  }]);

  return ProjectInfoController;
}();

ProjectInfoController.$inject = ['$filter', '$mdDialog', '$rootScope', '$state', '$stateParams', '$scope', '$timeout', 'ConfigService', 'ProjectService', 'UtilService'];

exports.default = ProjectInfoController;
//# sourceMappingURL=projectInfoController.js.map
