import { useParams, useNavigate, Link } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { ArrowLeft, Pencil, Trash2, Info } from "lucide-react";
import { fetchThought, deleteThought } from "@/lib/api";
import { Button } from "@/components/ui/button";
import StatusBadge from "@/components/StatusBadge";
import LoadingSpinner from "@/components/LoadingSpinner";
import { useToast } from "@/hooks/use-toast";
import {
  AlertDialog, AlertDialogAction, AlertDialogCancel,
  AlertDialogContent, AlertDialogDescription, AlertDialogFooter,
  AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger,
} from "@/components/ui/alert-dialog";

function formatDate(d: string) {
  return new Date(d).toLocaleString("en-US", { month: "short", day: "numeric", year: "numeric", hour: "2-digit", minute: "2-digit" });
}

export default function ThoughtDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { toast } = useToast();
  const queryClient = useQueryClient();

  const { data: thought, isLoading, error } = useQuery({
    queryKey: ["thought", id],
    queryFn: () => fetchThought(id!),
    enabled: !!id,
  });

  const deleteMutation = useMutation({
    mutationFn: () => deleteThought(id!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["thoughts"] });
      toast({ title: "Thought deleted" });
      navigate("/thoughts");
    },
    onError: () => toast({ title: "Failed to delete", variant: "destructive" }),
  });

  if (isLoading) return <LoadingSpinner />;
  if (error || !thought) return <div className="text-destructive">Failed to load thought.</div>;

  return (
    <div className="space-y-6">
      <Button variant="outline" size="sm" asChild>
        <Link to="/thoughts"><ArrowLeft size={16} /> Back to Thoughts</Link>
      </Button>

      <div className="rounded-lg border bg-card">
        <div className="flex items-center justify-between border-b px-6 py-4">
          <h1 className="text-2xl font-display">Thought Detail</h1>
          <div className="flex gap-2">
            <Button variant="outline" className="border-warning text-warning hover:bg-warning/10" asChild>
              <Link to={`/thoughts/${id}/edit`}><Pencil size={16} /> Edit</Link>
            </Button>
            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button variant="destructive"><Trash2 size={16} /> Delete</Button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogHeader>
                  <AlertDialogTitle>Delete this thought?</AlertDialogTitle>
                  <AlertDialogDescription>This action cannot be undone.</AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel>Cancel</AlertDialogCancel>
                  <AlertDialogAction onClick={() => deleteMutation.mutate()} className="bg-destructive text-destructive-foreground hover:bg-destructive/90">
                    Delete
                  </AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          </div>
        </div>
        <div className="space-y-6 p-6">
          <p className="text-lg leading-relaxed">{thought.content}</p>
          <div className="grid gap-4 sm:grid-cols-3">
            <div><span className="text-sm text-muted-foreground">Author</span><p className="font-medium">{thought.author}</p></div>
            <div><span className="text-sm text-muted-foreground">Author Bio</span><p className="font-medium">{thought.authorBio}</p></div>
            <div><span className="text-sm text-muted-foreground">Status</span><div className="mt-1"><StatusBadge status={thought.status} /></div></div>
          </div>
          <div className="grid gap-4 sm:grid-cols-4">
            <div><span className="text-sm text-muted-foreground">Thumbs Up</span><p className="text-xl font-bold text-success">{thought.thumbsUp}</p></div>
            <div><span className="text-sm text-muted-foreground">Thumbs Down</span><p className="text-xl font-bold text-destructive">{thought.thumbsDown}</p></div>
            <div><span className="text-sm text-muted-foreground">Created</span><p className="font-medium">{formatDate(thought.createdAt)}</p></div>
            <div><span className="text-sm text-muted-foreground">Updated</span><p className="font-medium">{formatDate(thought.updatedAt)}</p></div>
          </div>
        </div>
      </div>

      <div>
        <h2 className="mb-4 text-xl font-display">AI Evaluations</h2>
        <div className="flex items-center gap-3 rounded-lg border border-primary/20 bg-primary/5 p-4 text-sm">
          <Info size={20} className="text-primary" />
          No evaluations available for this thought.
        </div>
      </div>
    </div>
  );
}
