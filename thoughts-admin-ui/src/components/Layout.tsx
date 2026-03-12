import { Link, useLocation, useNavigate, Outlet } from "react-router-dom";
import { LayoutDashboard, MessageSquare, ThumbsUp, Brain, LogOut } from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";

const navLinks = [
  { to: "/", label: "Dashboard", icon: LayoutDashboard },
  { to: "/thoughts", label: "Thoughts", icon: MessageSquare },
  { to: "/ratings", label: "Ratings", icon: ThumbsUp },
  { to: "/evaluations", label: "Evaluations", icon: Brain },
];

export default function Layout() {
  const location = useLocation();
  const navigate = useNavigate();
  const { logout } = useAuth();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <div className="flex min-h-screen flex-col">
      <nav className="bg-nav">
        <div className="container flex h-14 items-center gap-8">
          <Link to="/" className="font-display text-lg text-nav-foreground">
            Thoughts Admin
          </Link>
          <div className="flex flex-1 gap-1">
            {navLinks.map(({ to, label, icon: Icon }) => {
              const active = to === "/" ? location.pathname === "/" : location.pathname.startsWith(to);
              return (
                <Link
                  key={to}
                  to={to}
                  className={`flex items-center gap-1.5 rounded-md px-3 py-1.5 text-sm font-medium transition-colors ${
                    active
                      ? "bg-primary-foreground/20 text-nav-foreground"
                      : "text-nav-foreground/80 hover:bg-primary-foreground/10 hover:text-nav-foreground"
                  }`}
                >
                  <Icon size={16} />
                  {label}
                </Link>
              );
            })}
          </div>
          <button
            onClick={handleLogout}
            className="flex items-center gap-1.5 rounded-md px-3 py-1.5 text-sm font-medium text-nav-foreground/80 hover:bg-primary-foreground/10 hover:text-nav-foreground transition-colors"
          >
            <LogOut size={16} />
            Logout
          </button>
        </div>
      </nav>

      <main className="flex-1">
        <div className="container py-8">
          <Outlet />
        </div>
      </main>

      <footer className="bg-footer py-6">
        <div className="container text-center text-sm text-footer-foreground/80">
          Positive Thoughts Admin — A Red Hat Demo Application
        </div>
      </footer>
    </div>
  );
}
