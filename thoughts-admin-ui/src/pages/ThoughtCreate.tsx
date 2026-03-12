import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createThought, type CreateThoughtInput } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { useToast } from "@/hooks/use-toast";

export default function ThoughtCreate() {
  const navigate = useNavigate();
  const { toast } = useToast();
  const queryClient = useQueryClient();
  const [form, setForm] = useState<CreateThoughtInput>({ content: "", author: "", authorBio: "" });
  const [errors, setErrors] = useState<Record<string, string>>({});

  const mutation = useMutation({
    mutationFn: () => createThought(form),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["thoughts"] });
      toast({ title: "Thought created!" });
      navigate(`/thoughts/${data.id}`);
    },
    onError: () => toast({ title: "Failed to create thought", variant: "destructive" }),
  });

  function validate() {
    const e: Record<string, string> = {};
    if (form.content.length < 10) e.content = "Content must be at least 10 characters.";
    if (form.content.length > 500) e.content = "Content must be at most 500 characters.";
    if (form.author.length > 200) e.author = "Author must be at most 200 characters.";
    if (form.authorBio.length > 200) e.authorBio = "Bio must be at most 200 characters.";
    setErrors(e);
    return Object.keys(e).length === 0;
  }

  function handleSubmit(ev: React.FormEvent) {
    ev.preventDefault();
    if (validate()) mutation.mutate();
  }

  return (
    <div className="mx-auto max-w-2xl space-y-6">
      <h1 className="text-3xl font-display">Create Thought</h1>
      <form onSubmit={handleSubmit} className="rounded-lg border bg-card p-6 space-y-5">
        <div>
          <label className="mb-1.5 block text-sm font-medium">Content *</label>
          <textarea
            className={`form-focus w-full rounded-md border bg-background px-3 py-2 text-sm ${errors.content ? "border-destructive" : ""}`}
            rows={4}
            value={form.content}
            onChange={(e) => setForm((f) => ({ ...f, content: e.target.value }))}
          />
          <p className="mt-1 text-xs text-muted-foreground">10-500 characters. {form.content.length}/500</p>
          {errors.content && <p className="mt-1 text-xs text-destructive">{errors.content}</p>}
        </div>
        <div>
          <label className="mb-1.5 block text-sm font-medium">Author</label>
          <input
            type="text"
            className={`form-focus w-full rounded-md border bg-background px-3 py-2 text-sm ${errors.author ? "border-destructive" : ""}`}
            value={form.author}
            onChange={(e) => setForm((f) => ({ ...f, author: e.target.value }))}
            maxLength={200}
          />
          {errors.author && <p className="mt-1 text-xs text-destructive">{errors.author}</p>}
        </div>
        <div>
          <label className="mb-1.5 block text-sm font-medium">Author Bio</label>
          <input
            type="text"
            className={`form-focus w-full rounded-md border bg-background px-3 py-2 text-sm ${errors.authorBio ? "border-destructive" : ""}`}
            value={form.authorBio}
            onChange={(e) => setForm((f) => ({ ...f, authorBio: e.target.value }))}
            maxLength={200}
          />
          {errors.authorBio && <p className="mt-1 text-xs text-destructive">{errors.authorBio}</p>}
        </div>
        <div className="flex gap-3">
          <Button type="submit" disabled={mutation.isPending}>
            {mutation.isPending ? "Creating…" : "Create Thought"}
          </Button>
          <Button type="button" variant="outline" asChild>
            <Link to="/thoughts">Cancel</Link>
          </Button>
        </div>
      </form>
    </div>
  );
}
