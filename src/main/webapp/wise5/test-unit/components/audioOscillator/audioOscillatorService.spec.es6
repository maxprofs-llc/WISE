import angular from 'angular';
import mainModule from 'vle/main';
import 'angular-mocks';

describe('AudioOscillatorService', () => {

  let AudioOscillatorService;

  beforeEach(angular.mock.module(mainModule.name));

  beforeEach(inject((_AudioOscillatorService_) => {
    AudioOscillatorService = _AudioOscillatorService_;
  }));

  it('should detect that a component state has student work', () => {
    const componentState = {
      studentData: {
        frequenciesPlayed: [440]
      }
    };
    const hasStudentWork = AudioOscillatorService.componentStateHasStudentWork(componentState);
    expect(hasStudentWork).toEqual(true);
  });

  it('should detect that a component state does not have student work', () => {
    const componentState = {
      studentData: {
        frequenciesPlayed: []
      }
    };
    const hasStudentWork = AudioOscillatorService.componentStateHasStudentWork(componentState);
    expect(hasStudentWork).toEqual(false);
  });

});
