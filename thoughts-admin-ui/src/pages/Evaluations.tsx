import { Link } from "react-router-dom";
import { BarChart3, Database, Loader2 } from "lucide-react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useToast } from "@/hooks/use-toast";
import { fetchVectorStatus, initializeVectors } from "@/lib/api";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";

export default function Evaluations() {
  const { toast } = useToast();
  const queryClient = useQueryClient();

  const { data: status, isLoading, error } = useQuery({
    queryKey: ["vectorStatus"],
    queryFn: fetchVectorStatus,
  });

  const mutation = useMutation({
    mutationFn: initializeVectors,
    onSuccess: (result) => {
      toast({ title: "Vectors initialized", description: result.message });
      queryClient.invalidateQueries({ queryKey: ["vectorStatus"] });
    },
    onError: (err: Error) => {
      toast({ title: "Initialization failed", description: err.message, variant: "destructive" });
    },
  });

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-display">AI Evaluations</h1>
        <Button variant="outline" asChild>
          <Link to="/evaluations/stats"><BarChart3 size={16} /> View Statistics</Link>
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Database size={20} />
            Vector Database Status
            {status && (
              <Badge variant={status.initialized ? "default" : "secondary"}>
                {status.initialized ? "Initialized" : "Not Initialized"}
              </Badge>
            )}
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {isLoading && <p className="text-sm text-muted-foreground">Loading vector status...</p>}
          {error && <p className="text-sm text-destructive">Failed to load vector status. Is the evaluation service running?</p>}
          {status && (
            <div className="grid grid-cols-3 gap-4">
              <div className="rounded-lg border p-3 text-center">
                <p className="text-2xl font-bold">{status.totalVectors}</p>
                <p className="text-xs text-muted-foreground">Total Vectors</p>
              </div>
              <div className="rounded-lg border p-3 text-center">
                <p className="text-2xl font-bold text-green-600">{status.positiveCount}</p>
                <p className="text-xs text-muted-foreground">Positive</p>
              </div>
              <div className="rounded-lg border p-3 text-center">
                <p className="text-2xl font-bold text-red-600">{status.negativeCount}</p>
                <p className="text-xs text-muted-foreground">Negative</p>
              </div>
            </div>
          )}

          <AlertDialog>
            <AlertDialogTrigger asChild>
              <Button disabled={mutation.isPending}>
                {mutation.isPending && <Loader2 size={16} className="mr-2 animate-spin" />}
                {mutation.isPending ? "Initializing..." : "Initialize Vector Database"}
              </Button>
            </AlertDialogTrigger>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>Initialize Vector Database?</AlertDialogTitle>
                <AlertDialogDescription>
                  This will delete all existing vectors and re-create them with real embeddings from the AI model.
                  This operation may take a moment as it generates embeddings for each reference phrase.
                </AlertDialogDescription>
              </AlertDialogHeader>
              <AlertDialogFooter>
                <AlertDialogCancel>Cancel</AlertDialogCancel>
                <AlertDialogAction onClick={() => mutation.mutate()}>Initialize</AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </CardContent>
      </Card>
    </div>
  );
}
