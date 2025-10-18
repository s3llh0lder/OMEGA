import { createBrowserRouter, Navigate } from 'react-router-dom';
import { Layout } from '@/components/layout/Layout';
import { AuthGuard } from '@/components/auth/AuthGuard';
import { LandingPage } from '@/pages/LandingPage';
import { GameLibraryPage } from '@/pages/GameLibraryPage';
import { GamePlayPage } from '@/pages/GamePlayPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <Layout />,
    children: [
      {
        index: true,
        element: <LandingPage />,
      },
      {
        path: '/games',
        element: (
          <AuthGuard>
            <GameLibraryPage />
          </AuthGuard>
        ),
      },
      {
        path: '/games/:gameId',
        element: (
          <AuthGuard>
            <GamePlayPage />
          </AuthGuard>
        ),
      },
      {
        path: '*',
        element: <Navigate to="/" replace />,
      },
    ],
  },
]);
