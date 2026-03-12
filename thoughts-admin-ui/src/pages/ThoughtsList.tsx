import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import { Plus } from "lucide-react";
import { fetchThoughts, type Thought } from "@/lib/api";
import { Button } from "@/components/ui/button";
import StatusBadge from "@/components/StatusBadge";
import LoadingSpinner from "@/components/LoadingSpinner";

function truncate(s: string, n: number) {
  return s.length > n ? s.slice(0, n) + "…" : s;
}
function formatDate(d: string) {
  return new Date(d).toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" });
}

const PAGE_SIZE = 20;

export default function ThoughtsList() {
  const [page, setPage] = useState(0);

  const { data: thoughts, isLoading, error } = useQuery<Thought[]>({
    queryKey: ["thoughts", page],
    queryFn: () => fetchThoughts(page, PAGE_SIZE),
  });

  if (isLoading) return <LoadingSpinner />;
  if (error) return <div className="text-destructive">Failed to load thoughts.</div>;

  const list = thoughts ?? [];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-display">Thoughts</h1>
        <Button asChild>
          <Link to="/thoughts/create"><Plus size={16} /> Create New Thought</Link>
        </Button>
      </div>

      <div className="overflow-hidden rounded-lg border">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-table-header text-table-header-foreground">
              <th className="px-4 py-3 text-left font-medium">Content</th>
              <th className="px-4 py-3 text-left font-medium">Author</th>
              <th className="px-4 py-3 text-left font-medium">Status</th>
              <th className="px-4 py-3 text-left font-medium">👍</th>
              <th className="px-4 py-3 text-left font-medium">👎</th>
              <th className="px-4 py-3 text-left font-medium">Created</th>
            </tr>
          </thead>
          <tbody>
            {list.map((t) => (
              <tr key={t.id} className="border-t">
                <td className="px-4 py-3">
                  <Link to={`/thoughts/${t.id}`} className="text-foreground hover:underline">
                    {truncate(t.content, 80)}
                  </Link>
                </td>
                <td className="px-4 py-3">{t.author}</td>
                <td className="px-4 py-3"><StatusBadge status={t.status} /></td>
                <td className="px-4 py-3 text-success">{t.thumbsUp}</td>
                <td className="px-4 py-3 text-destructive">{t.thumbsDown}</td>
                <td className="px-4 py-3 text-muted-foreground">{formatDate(t.createdAt)}</td>
              </tr>
            ))}
            {list.length === 0 && (
              <tr><td colSpan={6} className="px-4 py-8 text-center text-muted-foreground">No thoughts found.</td></tr>
            )}
          </tbody>
        </table>
      </div>

      <div className="flex items-center justify-center gap-4">
        <Button variant="outline" size="sm" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}>
          Previous
        </Button>
        <span className="text-sm text-muted-foreground">Page {page + 1}</span>
        <Button variant="outline" size="sm" onClick={() => setPage((p) => p + 1)} disabled={list.length < PAGE_SIZE}>
          Next
        </Button>
      </div>
    </div>
  );
}
