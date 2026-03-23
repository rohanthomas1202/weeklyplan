import type { PlanningWeekStatus } from '../../api/types';
import './StepIndicator.css';

interface StepIndicatorProps {
  totalCommits: number;
  reconciledCommits: number;
  weekStatus: PlanningWeekStatus;
}

interface StepDef {
  number: number;
  label: string;
}

const STEPS: StepDef[] = [
  { number: 1, label: 'Review Commits' },
  { number: 2, label: 'Week Summary' },
  { number: 3, label: 'Submit' },
];

export function StepIndicator({ totalCommits, reconciledCommits, weekStatus }: StepIndicatorProps) {
  // Determine which step is active (1-indexed)
  // All complete when RECONCILED
  const allReconciled = weekStatus === 'RECONCILED';
  const commitsAllDone = reconciledCommits === totalCommits && totalCommits > 0;

  // activeStep: the current step number (1, 2, or 3)
  // A step is "completed" if its number < activeStep, or if allReconciled
  let activeStep: number;
  if (allReconciled) {
    activeStep = 4; // past step 3, so all complete
  } else if (commitsAllDone) {
    activeStep = 2; // steps 2 and 3 both activate together
  } else {
    activeStep = 1;
  }

  return (
    <div className="step-indicator">
      {STEPS.map((step, index) => {
        const isCompleted = allReconciled || step.number < activeStep;
        const isActive = !allReconciled && step.number === activeStep;
        const isUpcoming = !allReconciled && step.number > activeStep;

        let circleClass = 'step-indicator__circle';
        if (isCompleted) circleClass += ' step-indicator__circle--completed';
        else if (isActive) circleClass += ' step-indicator__circle--active';
        else circleClass += ' step-indicator__circle--upcoming';

        const showConnector = index < STEPS.length - 1;
        const connectorFilled = allReconciled || activeStep > step.number + 1 ||
          (commitsAllDone && step.number >= 1);

        return (
          <div key={step.number} className="step-indicator__item">
            <div className="step-indicator__step">
              <div className={circleClass}>
                {isCompleted ? (
                  <span className="step-indicator__check">✓</span>
                ) : (
                  <span className="step-indicator__number">{step.number}</span>
                )}
              </div>
              <span className={`step-indicator__label${isUpcoming ? ' step-indicator__label--upcoming' : ''}`}>
                {step.label}
              </span>
            </div>
            {showConnector && (
              <div className={`step-indicator__connector${connectorFilled ? ' step-indicator__connector--filled' : ''}`} />
            )}
          </div>
        );
      })}
    </div>
  );
}
