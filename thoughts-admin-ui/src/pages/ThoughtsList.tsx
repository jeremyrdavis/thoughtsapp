import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Link, useSearchParams } from "react-router-dom";
import { Plus } from "lucide-react";
import { fetchThoughts, fetchAllThoughts, type Thought } from "@/lib/api";
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

const STATUS_FILTERS = [
  { label: "All", value: "" },
  { label: "Approved", value: "APPROVED" },
  { label: "Rejected", value: "REJECTED" },
  { label: "In Review", value: "IN_REVIEW" },
] as const;

export default function ThoughtsList() {
  const [searchParams, setSearchParams] = useSearchParams();
  const statusFilter = searchParams.get("status") ?? "";
  const [page, setPage] = useState(0);

  const pagedQuery = useQuery<Thought[]>({
    queryKey: ["thoughts", page],
    queryFn: () => fetchThoughts(page, PAGE_SIZE),
    enabled: !statusFilter,
  });

  const allQuery = useQuery<Thought[]>({
    queryKey: ["thoughts-all"],
    queryFn: fetchAllThoughts,
    enabled: !!statusFilter,
  });

  const isLoading = statusFilter ? allQuery.isLoading : pagedQuery.isLoading;
  const error = statusFilter ? allQuery.error : pagedQuery.error;

  if (isLoading) return <LoadingSpinner />;
  if (error) return <div className="text-destructive">Failed to load thoughts.</div>;

  let list: Thought[];
  let hasMorePages: boolean;
  if (statusFilter) {
    const filtered = (allQuery.data ?? []).filter((t) => t.status === statusFilter);
    list = filtered.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE);
    hasMorePages = filtered.length > (page + 1) * PAGE_SIZE;
  } else {
    list = pagedQuery.data ?? [];
    hasMorePages = list.length >= PAGE_SIZE;
  }

  function setFilter(value: string) {
    setPage(0);
    if (value) {
      setSearchParams({ status: value });
    } else {
      setSearchParams({});
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-display">Thoughts</h1>
        <Button asChild>
          <Link to="/thoughts/create"><Plus size={16} /> Create New Thought</Link>
        </Button>
      </div>

      <div className="flex gap-2">
        {STATUS_FILTERS.map(({ label, value }) => (
          <Button
            key={value}
            variant={statusFilter === value ? "default" : "outline"}
            size="sm"
            onClick={() => setFilter(value)}
          >
            {label}
          </Button>
        ))}
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
        <Button variant="outline" size="sm" onClick={() => setPage((p) => p + 1)} disabled={!hasMorePages}>
          Next
        </Button>
      </div>
    </div>
  );
}
