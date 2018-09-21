import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { StudentRun } from '../student-run';
import { StudentRunListItemComponent } from './student-run-list-item.component';
import { Observable } from "rxjs";
import { Config } from "../../domain/config";
import { ConfigService } from "../../services/config.service";
import { MomentModule } from "ngx-moment";
import { MatCardModule } from "@angular/material";
import { Project } from "../../domain/project";
import { User } from "../../domain/user";

describe('StudentRunListItemComponent', () => {
  let component: StudentRunListItemComponent;
  let fixture: ComponentFixture<StudentRunListItemComponent>;

  beforeEach(async(() => {
    const configServiceStub = {
      getConfig(): Observable<Config> {
        const config : Config = {"contextPath":"vle","logOutURL":"/logout","currentTime":20180730};
        return Observable.create( observer => {
          observer.next(config);
          observer.complete();
        });
      },
      getContextPath(): string {
        return '/wise';
      }
    };
    TestBed.configureTestingModule({
      imports: [ MatCardModule, MomentModule ],
      declarations: [ StudentRunListItemComponent ],
      providers: [ { provide: ConfigService, useValue: configServiceStub } ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentRunListItemComponent);
    component = fixture.componentInstance;
    const run: StudentRun = new StudentRun();
    run.id = 1;
    run.name = "Photosynthesis";
    const owner = new User();
    owner.displayName = "Mr. Happy";
    run.owner = owner;
    run.projectThumb = "Happy.png";
    run.startTime = 20180612;
    const project: Project = new Project();
    project.id = 1;
    project.name = "Test Project";
    run.project = project;
    component.run = run;
    fixture.detectChanges();
  });

  it('should create', () => {
    try {
      expect(component).toBeTruthy();
    } catch (e) {
      console.log(e);
    }
  });

  it('should say a run is available', () => {
    expect(component.isAvailable).toBeTruthy();
  });

  it('should say a run is not available yet', () => {
    component.run.startTime = 20180801;
    component.ngOnInit();
    expect(component.isAvailable).toBeFalsy();
  });
});