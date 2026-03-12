import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";
import { Toaster } from "@/components/ui/toaster";
import { TooltipProvider } from "@/components/ui/tooltip";
import { AuthProvider, useAuth } from "@/contexts/AuthContext";
import Layout from "@/components/Layout";
import Dashboard from "@/pages/Dashboard";
import ThoughtsList from "@/pages/ThoughtsList";
import ThoughtDetail from "@/pages/ThoughtDetail";
import ThoughtCreate from "@/pages/ThoughtCreate";
import ThoughtEdit from "@/pages/ThoughtEdit";
import Ratings from "@/pages/Ratings";
import Evaluations from "@/pages/Evaluations";
import EvaluationStats from "@/pages/EvaluationStats";
import Login from "@/pages/Login";
import NotFound from "@/pages/NotFound";

const queryClient = new QueryClient();

function ProtectedRoutes() {
  const { isAuthenticated } = useAuth();
  if (!isAuthenticated) return <Navigate to="/login" replace />;

  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<Dashboard />} />
        <Route path="/thoughts" element={<ThoughtsList />} />
        <Route path="/thoughts/create" element={<ThoughtCreate />} />
        <Route path="/thoughts/:id" element={<ThoughtDetail />} />
        <Route path="/thoughts/:id/edit" element={<ThoughtEdit />} />
        <Route path="/ratings" element={<Ratings />} />
        <Route path="/evaluations" element={<Evaluations />} />
        <Route path="/evaluations/stats" element={<EvaluationStats />} />
      </Route>
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="*" element={<ProtectedRoutes />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
