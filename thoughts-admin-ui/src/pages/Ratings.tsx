import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import { fetchAllThoughts, type Thought } from "@/lib/api";
import { Button } from "@/components/ui/button";
import LoadingSpinner from "@/components/LoadingSpinner";

type SortMode = "most-rated" | "most-liked" | "most-disliked";

function truncate(s: string, n: number) {
  return s.length > n ? s.slice(0, n) + "…" : s;
}

export default function Ratings() {
  const [sort, setSort] = useState<SortMode>("most-rated");

  const { data: thoughts, isLoading, error } = useQuery<Thought[]>({
    queryKey: ["thoughts-all"],
    queryFn: fetchAllThoughts,
  });

  if (isLoading) return <LoadingSpinner />;
  if (error) return <div className="text-destructive">Failed to load data.</div>;

  const sorted = [...(thoughts ?? [])]
    .filter((t) => {
      if (sort === "most-liked") return t.thumbsUp > 0;
      if (sort === "most-disliked") return t.thumbsDown > 0;
      return t.thumbsUp + t.thumbsDown > 0;
    })
    .sort((a, b) => {
      if (sort === "most-rated") return (b.thumbsUp + b.thumbsDown) - (a.thumbsUp + a.thumbsDown);
      if (sort === "most-liked") return b.thumbsUp - a.thumbsUp;
      return b.thumbsDown - a.thumbsDown;
    });

  const modes: { key: SortMode; label: string }[] = [
    { key: "most-rated", label: "Most Rated" },
    { key: "most-liked", label: "Most Liked" },
    { key: "most-disliked", label: "Most Disliked" },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-display">Ratings Overview</h1>
      <div className="flex gap-1">
        {modes.map((m) => (
          <Button
            key={m.key}
            variant={sort === m.key ? "default" : "outline"}
            size="sm"
            onClick={() => setSort(m.key)}
          >
            {m.label}
          </Button>
        ))}
      </div>

      <div className="overflow-hidden rounded-lg border">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-table-header text-table-header-foreground">
              <th className="px-4 py-3 text-left font-medium">Content</th>
              <th className="px-4 py-3 text-left font-medium">👍</th>
              <th className="px-4 py-3 text-left font-medium">👎</th>
              <th className="px-4 py-3 text-left font-medium">Net Score</th>
            </tr>
          </thead>
          <tbody>
            {sorted.map((t) => (
              <tr key={t.id} className="border-t">
                <td className="px-4 py-3">
                  <Link to={`/thoughts/${t.id}`} className="text-foreground hover:underline">
                    {truncate(t.content, 60)}
                  </Link>
                </td>
                <td className="px-4 py-3 text-success">{t.thumbsUp}</td>
                <td className="px-4 py-3 text-destructive">{t.thumbsDown}</td>
                <td className="px-4 py-3 font-bold">{t.thumbsUp - t.thumbsDown}</td>
              </tr>
            ))}
            {sorted.length === 0 && (
              <tr><td colSpan={4} className="px-4 py-8 text-center text-muted-foreground">No thoughts found.</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
