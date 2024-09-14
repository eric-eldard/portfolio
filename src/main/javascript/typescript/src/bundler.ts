// Shim to attach module namespaces to global context
import { Portfolio } from './portfolio';
import { UserManagement } from './user-management';
import { Video } from './video';

(window as any).Portfolio = Portfolio;
(window as any).UserManagement = UserManagement;
(window as any).Video = Video;