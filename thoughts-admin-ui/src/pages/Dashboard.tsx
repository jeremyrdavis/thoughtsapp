import { useQuery } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import { Lightbulb, ThumbsUp, ThumbsDown, CheckCircle, XCircle, Hourglass } from "lucide-react";
import { fetchAllThoughts, type Thought } from "@/lib/api";
import StatusBadge from "@/components/StatusBadge";
import LoadingSpinner from "@/components/LoadingSpinner";

function truncate(s: string, n: number) {
  return s.length > n ? s.slice(0, n) + "…" : s;
}

function formatDate(d: string) {
  return new Date(d).toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" });
}

export default function Dashboard() {
  const { data: thoughts, isLoading, error } = useQuery<Thought[]>({
    queryKey: ["thoughts-all"],
    queryFn: fetchAllThoughts,
  });

  if (isLoading) return <LoadingSpinner />;
  if (error) return <div className="text-destructive">Failed to load data.</div>;

  const all = thoughts ?? [];
  const totalUp = all.reduce((s, t) => s + t.thumbsUp, 0);
  const totalDown = all.reduce((s, t) => s + t.thumbsDown, 0);
  const approved = all.filter((t) => t.status === "APPROVED").length;
  const rejected = all.filter((t) => t.status === "REJECTED").length;
  const inReview = all.filter((t) => t.status === "IN_REVIEW").length;
  const recent = [...all].sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()).slice(0, 5);

  return (
    <div className="space-y-8">
      <h1 className="text-3xl font-display">Dashboard</h1>

      {/* Stat cards */}
      <div className="grid gap-6 md:grid-cols-3">
        <div className="stat-card stat-card-primary flex items-center gap-4">
          <Lightbulb size={40} className="opacity-80" />
          <div>
            <p className="text-sm opacity-80">Total Thoughts</p>
            <p className="text-3xl font-bold">{all.length}</p>
          </div>
        </div>
        <div className="stat-card stat-card-success flex items-center gap-4">
          <ThumbsUp size={40} className="opacity-80" />
          <div>
            <p className="text-sm opacity-80">Total Thumbs Up</p>
            <p className="text-3xl font-bold">{totalUp}</p>
          </div>
        </div>
        <div className="stat-card stat-card-danger flex items-center gap-4">
          <ThumbsDown size={40} className="opacity-80" />
          <div>
            <p className="text-sm opacity-80">Total Thumbs Down</p>
            <p className="text-3xl font-bold">{totalDown}</p>
          </div>
        </div>
      </div>

      {/* Status cards */}
      <div className="grid gap-6 md:grid-cols-3">
        {[
          { label: "Approved", status: "APPROVED", count: approved, icon: CheckCircle, borderColor: "border-success", bgColor: "bg-success/5" },
          { label: "Rejected", status: "REJECTED", count: rejected, icon: XCircle, borderColor: "border-destructive", bgColor: "bg-destructive/5" },
          { label: "In Review", status: "IN_REVIEW", count: inReview, icon: Hourglass, borderColor: "border-warning", bgColor: "bg-warning/5" },
        ].map(({ label, status, count, icon: Icon, borderColor, bgColor }) => (
          <Link key={label} to={`/thoughts?status=${status}`} className={`rounded-lg border-2 ${borderColor} ${bgColor} p-6 cursor-pointer transition-opacity hover:opacity-80`}>
            <div className="flex items-center gap-3">
              <Icon size={24} className="text-muted-foreground" />
              <div>
                <p className="text-sm text-muted-foreground">{label}</p>
                <p className="text-2xl font-bold">{count}</p>
              </div>
            </div>
          </Link>
        ))}
      </div>

      {/* Recent Activity */}
      <div>
        <h2 className="mb-4 text-xl font-display">Recent Activity</h2>
        <div className="overflow-hidden rounded-lg border">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-table-header text-table-header-foreground">
                <th className="px-4 py-3 text-left font-medium">Content</th>
                <th className="px-4 py-3 text-left font-medium">Author</th>
                <th className="px-4 py-3 text-left font-medium">Status</th>
                <th className="px-4 py-3 text-left font-medium">Updated</th>
              </tr>
            </thead>
            <tbody>
              {recent.map((t) => (
                <tr key={t.id} className="border-t">
                  <td className="px-4 py-3">
                    <Link to={`/thoughts/${t.id}`} className="text-foreground hover:underline">
                      {truncate(t.content, 50)}
                    </Link>
                  </td>
                  <td className="px-4 py-3">{t.author}</td>
                  <td className="px-4 py-3"><StatusBadge status={t.status} /></td>
                  <td className="px-4 py-3 text-muted-foreground">{formatDate(t.updatedAt)}</td>
                </tr>
              ))}
              {recent.length === 0 && (
                <tr><td colSpan={4} className="px-4 py-8 text-center text-muted-foreground">No thoughts yet.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
