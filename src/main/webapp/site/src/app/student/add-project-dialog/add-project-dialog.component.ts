import { Component, OnInit } from '@angular/core';
import { MatDialog } from "@angular/material";
import { StudentService } from "../student.service";
import { AbstractControl, FormControl, FormGroup, ValidatorFn, Validators } from "@angular/forms";

@Component({
  selector: 'app-add-project-dialog',
  templateUrl: './add-project-dialog.component.html',
  styleUrls: ['./add-project-dialog.component.scss']
})
export class AddProjectDialogComponent implements OnInit {

  validRunCodeSyntaxRegEx: any = /^[a-zA-Z]*\d\d\d$/;
  registerRunRunCode: string = '';
  registerRunPeriods: string[] = [];
  selectedPeriod: string = '';
  runCodeFormControl = new FormControl('', [runCodeValidator(this.validRunCodeSyntaxRegEx)]);
  addProjectForm: FormGroup = new FormGroup({
    runCode: this.runCodeFormControl,
    period: new FormControl({value: '', disabled: true}, Validators.required)
  });
  isAdding = false;

  constructor(public dialog: MatDialog,
              private studentService: StudentService) {
  }

  ngOnInit() {
  }

  submit() {
    this.isAdding = true;
    this.studentService.addRun(this.registerRunRunCode, this.selectedPeriod).subscribe((studentRun) => {
      if (studentRun.error) {
        this.addProjectForm.controls['runCode'].setErrors({'alreadyAddedRun': true});
        this.isAdding = false;
      } else {
        this.studentService.addNewProject(studentRun);
        this.dialog.closeAll();
        this.isAdding = false;
      }
    });
  }

  clearPeriods() {
    this.selectedPeriod = '';
    this.registerRunPeriods = [];
    this.addProjectForm.controls['period'].disable();
  }

  checkRunCode(event: KeyboardEvent) {
    const runCode = (<HTMLInputElement>event.target).value;
    this.registerRunRunCode = runCode;
    if (this.isValidRunCodeSyntax(runCode)) {
      this.studentService.getRunInfo(runCode).subscribe(runInfo => {
        if (runInfo.error) {
          this.clearPeriods();
          this.addProjectForm.controls['runCode'].setErrors({'invalidRunCode': true});
        } else {
          this.registerRunPeriods = runInfo.periods;
          this.addProjectForm.controls['period'].enable();
        }
      });
    } else {
      this.clearPeriods();
    }
  }

  isValidRunCodeSyntax(runCode: string) {
    return this.validRunCodeSyntaxRegEx.test(runCode);
  }
}

export function runCodeValidator(validRunCodeSyntaxRegEx: any): ValidatorFn {
  return (control: AbstractControl): {[key: string]: any} | null => {
    const valid = validRunCodeSyntaxRegEx.test(control.value);
    return valid ? null : {'invalidRunCodeSyntax': true};
  };
}
