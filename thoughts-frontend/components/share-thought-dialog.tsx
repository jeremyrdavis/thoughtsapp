'use client';

import { useState } from 'react';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { toast } from 'sonner';
import { apiClient } from '@/lib/api-client';

const MIN_CONTENT_LENGTH = 10;
const MAX_CONTENT_LENGTH = 500;
const MAX_AUTHOR_LENGTH = 200;

interface ShareThoughtDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function ShareThoughtDialog({ open, onOpenChange }: ShareThoughtDialogProps) {
  const [content, setContent] = useState('');
  const [author, setAuthor] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const contentLength = content.length;
  const isContentValid = contentLength >= MIN_CONTENT_LENGTH && contentLength <= MAX_CONTENT_LENGTH;
  const isAuthorValid = author.trim().length > 0 && author.length <= MAX_AUTHOR_LENGTH;
  const isFormValid = isContentValid && isAuthorValid;

  const resetForm = () => {
    setContent('');
    setAuthor('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid || submitting) return;

    setSubmitting(true);
    try {
      await apiClient.createThought({ content: content.trim(), author: author.trim() });
      toast.success('Your thought has been submitted for review!');
      resetForm();
      onOpenChange(false);
    } catch {
      toast.error('Failed to submit your thought. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleOpenChange = (newOpen: boolean) => {
    if (!newOpen) {
      resetForm();
    }
    onOpenChange(newOpen);
  };

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Share Your Thought</DialogTitle>
          <DialogDescription>
            Share an inspiring thought or quote. It will be reviewed before being published.
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="content">Your Thought</Label>
            <Textarea
              id="content"
              placeholder="Enter your thought..."
              value={content}
              onChange={(e) => setContent(e.target.value.slice(0, MAX_CONTENT_LENGTH))}
              rows={4}
              required
              minLength={MIN_CONTENT_LENGTH}
              maxLength={MAX_CONTENT_LENGTH}
            />
            <p className={`text-xs ${contentLength < MIN_CONTENT_LENGTH ? 'text-muted-foreground' : contentLength > MAX_CONTENT_LENGTH * 0.9 ? 'text-orange-500' : 'text-muted-foreground'}`}>
              {contentLength}/{MAX_CONTENT_LENGTH} characters (minimum {MIN_CONTENT_LENGTH})
            </p>
          </div>
          <div className="space-y-2">
            <Label htmlFor="author">Your Name</Label>
            <Input
              id="author"
              placeholder="Enter your name"
              value={author}
              onChange={(e) => setAuthor(e.target.value.slice(0, MAX_AUTHOR_LENGTH))}
              required
              maxLength={MAX_AUTHOR_LENGTH}
            />
          </div>
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => handleOpenChange(false)}
              disabled={submitting}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={!isFormValid || submitting}>
              {submitting ? 'Submitting...' : 'Submit'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
