export type PlanningWeekStatus = 'DRAFT' | 'LOCKED' | 'RECONCILING' | 'RECONCILED';
export type CommitDisposition = 'COMPLETED' | 'PARTIALLY_COMPLETED' | 'NOT_COMPLETED' | 'CARRIED_FORWARD' | 'DROPPED';
export type UserRole = 'IC' | 'MANAGER';

export interface UserDto {
  id: number;
  name: string;
  email: string;
  role: UserRole;
  teamId: number;
}

export interface ChessCategoryDto {
  code: string;
  displayName: string;
  description: string;
  sortOrder: number;
}

export interface OutcomeDto {
  id: number;
  title: string;
  description: string | null;
}

export interface StrategyTreeNode {
  id: number;
  title: string;
  description: string | null;
  definingObjectives: {
    id: number;
    title: string;
    description: string | null;
    outcomes: OutcomeDto[];
  }[];
}

export interface ReconciliationDto {
  id: number;
  weeklyCommitId: number;
  disposition: CommitDisposition;
  actualResult: string | null;
  percentComplete: number | null;
  blockerNotes: string | null;
  carryForward: boolean;
  reconciliationNotes: string | null;
  reconciledAt: string;
}

export interface WeeklyCommitDto {
  id: number;
  title: string;
  description: string | null;
  rallyCryId: number;
  rallyCryTitle: string;
  definingObjectiveId: number;
  definingObjectiveTitle: string;
  outcomeId: number;
  outcomeTitle: string;
  chessCategoryCode: string;
  chessCategoryDisplayName: string;
  priorityRank: number;
  stretch: boolean;
  sourceCommitId: number | null;
  reconciliation: ReconciliationDto | null;
}

export interface PlanningWeekDto {
  id: number;
  userId: number;
  teamId: number;
  weekStartDate: string;
  weekEndDate: string;
  status: PlanningWeekStatus;
  lockedAt: string | null;
  reconcilingAt: string | null;
  reconciledAt: string | null;
  blockersSummary: string | null;
  managerNotes: string | null;
  commits: WeeklyCommitDto[];
}

export interface TeamWeekStatusDto {
  userId: number;
  userName: string;
  userRole: string;
  weekId: number | null;
  status: string | null;
  commitCount: number;
  weekStartDate: string | null;
}

export interface CreateCommitRequest {
  title: string;
  description?: string;
  rallyCryId: number;
  definingObjectiveId: number;
  outcomeId: number;
  chessCategoryCode: string;
  priorityRank: number;
  stretch?: boolean;
}

export interface ReconcileCommitRequest {
  disposition: CommitDisposition;
  actualResult?: string;
  percentComplete?: number;
  blockerNotes?: string;
  carryForward?: boolean;
  reconciliationNotes?: string;
}

export interface ReorderRequest {
  commitIds: number[];
}

export interface ErrorResponse {
  error: string;
  message: string;
}
